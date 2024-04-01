package mishin.trader.net.test.domain


data class Quotation(
    val ticker: String,
    val changePercent: Double? = null,
    var lastTradeExchange: String? = null,
    var name: String? = null,
    var lastTradePrice: Double? = null,
    var change: Double? = null,
    var minStep: Double? = null
)
