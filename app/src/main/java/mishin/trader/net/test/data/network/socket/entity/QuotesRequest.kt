package mishin.trader.net.test.data.network.socket.entity

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuotesRequest(
    @SerialName("quotes")
    val quotes: List<String>
)