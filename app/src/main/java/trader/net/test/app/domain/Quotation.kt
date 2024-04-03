package trader.net.test.app.domain


data class Quotation(
    val ticker: String,
    var changePercent: Double? = null,
    var lastTradeExchange: String = "",
    var name: String,
    var lastTradePrice: Double = 0.0,
    var change: Double = 0.0,
    var minStep: Double = 0.0,
    var changeType: ChangeType = ChangeType.NONE
) {
    enum class ChangeType {
        POSITIVE, NEGATIVE, NONE
    }
}
