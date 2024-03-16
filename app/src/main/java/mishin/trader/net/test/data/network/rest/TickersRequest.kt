package mishin.trader.net.test.data.network.rest

import kotlinx.serialization.Serializable

@Serializable
data class ApiParams(
    val cmd: String,
    val params: RequestParams = RequestParams()
)

@Serializable
data class RequestParams(
    val type: String = "stocks",
    val exchange: String = "russia",
    val gainers: Int = 0,
    val limit: Int = 10,
)