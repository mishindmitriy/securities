package trader.net.test.app.data.network.tickers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TickersResponse(
    @SerialName("tickers")
    val tickers: List<String?>?
)
