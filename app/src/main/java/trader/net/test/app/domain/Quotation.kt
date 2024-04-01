package trader.net.test.app.domain


data class Quotation(
    val ticker: String,
    val changePercent: Double? = null,
    var lastTradeExchange: String? = null,
    var name: String? = null,
    var lastTradePrice: Double? = null,
    var change: Double? = null,
    var minStep: Double? = null
) {
    val logoUrl: String get() = LOGO_URL + ticker.lowercase()

    companion object {
        private const val LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="
    }
}
