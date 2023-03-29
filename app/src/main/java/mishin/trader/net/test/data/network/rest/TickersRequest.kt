package mishin.trader.net.test.data.network.rest

@kotlinx.serialization.Serializable
data class ApiParams(
    val cmd: String,
    val params: RequestParams = RequestParams()
)

@kotlinx.serialization.Serializable
data class RequestParams(
    val type: String = "stocks",
    val exchange: String = "russia",
    val gainers: Int = 0,
    val limit: Int = 10,
)