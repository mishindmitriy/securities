package trader.net.test.app.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import trader.net.test.app.LOGO_URL
import trader.net.test.app.R
import trader.net.test.app.domain.Quotation
import trader.net.test.app.domain.QuotationsRepository
import trader.net.test.app.domain.ResourceProvider
import trader.net.test.app.domain.TickersRepository
import java.text.DecimalFormat

data class QuotationsViewState(
    val list: List<QuotationViewData> = listOf(),
    val inProgress: Boolean = true,
    val error: String? = null
)

class QuotationsViewModel(
    private val tickersRepository: TickersRepository,
    private val quotationsRepository: QuotationsRepository,
    private val resourceProvider: ResourceProvider,
    private val coroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow(QuotationsViewState())
    val state: StateFlow<QuotationsViewState> get() = _state

    private val decimalFormat = DecimalFormat(DECIMAL_FORMAT_PATTERN)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, throwable.message.toString())
        _state.update {
            it.copy(
                error = resourceProvider.getString(R.string.error_something_wrong),
                inProgress = false
            )
        }
    }

    private var subscriptionJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        subscriptionJob?.cancel()
        subscriptionJob = viewModelScope.launch(coroutineDispatcher + exceptionHandler) {
            quotationsRepository.getQuotations(tickersRepository.getTickers())
                .map { it.map { item -> item.mapToViewData() } }
                .collect { list ->
                    _state.update { it.copy(error = null, inProgress = false, list = list) }
                }
        }
    }

    private fun Quotation.mapToViewData() = QuotationViewData(
        ticker = ticker,
        name = String.format(NAME_PATTERN, lastTradeExchange, name),
        logoUrl = LOGO_URL + ticker.lowercase(),
        priceChange = formatPrice(change, lastTradePrice),
        percentColor = preparePercentColor(changePercent),
        percentChange = formatDecimal(changePercent) + PERCENTAGE_SYMBOL,
        animate = changeType
    )

    private fun formatDecimal(d: Double): String {
        val prefix = if (d > 0.0) POSITIVE_DECIMAL_PREFIX else ""
        return String.format(DECIMAL_WITH_PREFIX_PATTERN, prefix, decimalFormat.format(d))
    }

    private fun formatPrice(change: Double, lastTradePrice: Double): String {
        val changeString: String = formatDecimal(change)
        return String.format(PRICE_FORMAT_PATTERN, lastTradePrice, changeString)
    }

    private fun preparePercentColor(changePercent: Double) = resourceProvider.getColor(
        when {
            changePercent > 0 -> R.color.green
            changePercent < 0 -> R.color.red
            else -> R.color.black_opacity_90
        }
    )

    companion object {
        private const val TAG = "QUOTATIONS_VIEWMODEL"
        private const val NAME_PATTERN = "%s | %s"
        private const val DECIMAL_FORMAT_PATTERN = "###.#####"
        private const val POSITIVE_DECIMAL_PREFIX = "+"
        private const val DECIMAL_WITH_PREFIX_PATTERN = "%s%s"
        private const val PRICE_FORMAT_PATTERN = "%s (%s)"
        private const val PERCENTAGE_SYMBOL = "%"
    }
}
