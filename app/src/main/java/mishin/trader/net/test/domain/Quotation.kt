package mishin.trader.net.test.domain

data class Quotation(
    val ticker: String,
    val changePercent: Double? = null
)
