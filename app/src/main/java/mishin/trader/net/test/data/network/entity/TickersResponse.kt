package mishin.trader.net.test.data.network.entity

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TickersResponse(
    @SerialName("tickers")
    val tickers: List<String>
)