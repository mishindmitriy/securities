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
import java.math.BigDecimal
import java.math.MathContext
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
        name = NAME_PATTERN.format(lastTradeExchange, name),
        logoUrl = LOGO_URL + ticker.lowercase(),
        priceWithChange = formatPriceWithChange(change, lastTradePrice, minStep),
        percentColor = preparePercentColor(change),
        percentChange = formatDecimal(changePercent ?: 0.0) + PERCENTAGE_SYMBOL,
        animate = changeType
    )

    private fun formatDecimal(d: Double): String {
        val prefix = if (d > 0.0) POSITIVE_DECIMAL_PREFIX else ""
        return DECIMAL_WITH_PREFIX_PATTERN.format(prefix, decimalFormat.format(d))
    }

    private fun formatPriceWithChange(
        change: Double,
        lastTradePrice: Double,
        minStep: Double
    ): String {
        if (minStep == 0.0) return PRICE_FORMAT_PATTERN.format(lastTradePrice, change)
        val changeString: String = formatDecimal(roundByStepFormat(change, minStep).toDouble())
        val price = roundByStepFormat(lastTradePrice, minStep)
        return PRICE_FORMAT_PATTERN.format(price.toString(), changeString)
    }

    private fun roundByStepFormat(value: Double, step: Double): BigDecimal {
        val minStepBigDecimal = step.toBigDecimal()
        val underStep = value.toBigDecimal() / minStepBigDecimal
        val rounded = underStep.round(MathContext.DECIMAL32)
        val overMinStep = rounded * minStepBigDecimal
        return overMinStep.stripTrailingZeros()
    }

    private fun preparePercentColor(change: Double) = resourceProvider.getColor(
        when {
            change > 0 -> R.color.green
            change < 0 -> R.color.red
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
