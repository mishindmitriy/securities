package mishin.trader.net.test.data.network.rest.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TickersResponse(
    @SerialName("tickers")
    val tickers: List<String>
)
