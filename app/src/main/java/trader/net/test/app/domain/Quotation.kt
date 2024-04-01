package trader.net.test.app.domain


data class Quotation(
    val ticker: String,
    val changePercent: Double? = null,
    var lastTradeExchange: String? = null,
    var name: String,
    var lastTradePrice: Double? = null,
    var change: Double? = null,
    var minStep: Double? = null
)
