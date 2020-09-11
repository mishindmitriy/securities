package mishin.trader.net.test.network

data class RequestParams(
    val type: String, val exchange: String, val gainers: Int = 0, val limit: Int = 12,
)