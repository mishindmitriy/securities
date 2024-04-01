package mishin.trader.net.test.data.network.tickers

import kotlinx.serialization.Serializable

@Serializable
data class GetTickersRequest(
    val cmd: String = "getTopSecurities",
    val params: RequestParams = RequestParams()
)

@Serializable
data class RequestParams(
    val type: String = "stocks",
    val exchange: String = "russia",
    val gainers: Int = 0,
    val limit: Int = LIMIT_DEFAULT,
)

private const val LIMIT_DEFAULT = 30
