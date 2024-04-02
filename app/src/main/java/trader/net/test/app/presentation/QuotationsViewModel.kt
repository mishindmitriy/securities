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

    private val decimalFormat = DecimalFormat("###.#####")

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

    private fun Quotation.toViewData() = QuotationViewData(
        ticker = ticker,
        name = if (lastTradeExchange == null) name
        else String.format(NAME_PATTERN, lastTradeExchange, name),
        logoUrl = LOGO_URL + ticker.lowercase(),
        priceChange = formatPrice(this),
        percentColor = preparePercentColor(changePercent),
        percentChange = formatPercentage(changePercent)
    )

    private fun formatPercentage(changePercent: Double): String {
        val prefix = when {
            changePercent > 0.0 -> "+"
            else -> ""
        }
        return "$prefix${decimalFormat.format(changePercent)}%"
    }

    private fun formatPrice(q: Quotation): String {
        return if (q.lastTradePrice == null) ""
        else {
            val qChange = q.change
            val changeString: String = when {
                qChange == null -> ""
                qChange > 0.0 -> "+${decimalFormat.format(qChange)}"
                else -> decimalFormat.format(qChange)
            }
            "${q.lastTradePrice} ($changeString)"
        }
    }

    private fun preparePercentColor(changePercent: Double) = resourceProvider.getColor(
        when {
            changePercent > 0 -> R.color.green
            changePercent < 0 -> R.color.red
            else -> R.color.black_opacity_90
        }
    )

    fun loadData() {
        subscriptionJob?.cancel()
        subscriptionJob = viewModelScope.launch(coroutineDispatcher + exceptionHandler) {
            quotationsRepository.getQuotations(tickersRepository.getTickers())
                .map { it.map { item -> item.toViewData() } }
                .collect { list ->
                    _state.update { it.copy(error = null, inProgress = false, list = list) }
                }
        }
    }

    companion object {
        private const val TAG = "viewmodel"
        private const val LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="
        private const val NAME_PATTERN = "%s | %s"
    }
}
