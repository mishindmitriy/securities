package mishin.trader.net.test.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import mishin.trader.net.test.data.network.quotations.QuotationRawData
import mishin.trader.net.test.domain.Quotation
import mishin.trader.net.test.domain.QuotationsRepository
import mishin.trader.net.test.domain.Ticker

class QuotationsRepositoryImpl(
    private val json: Json,
    private val socketClient: HttpClient
) : QuotationsRepository {

    override suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>> {
        val quotationMutableMap: MutableMap<String, Quotation> = hashMapOf()
        tickers.forEach { quotationMutableMap[it.ticker] = Quotation(it.ticker) }

        return channelFlow {
            send(quotationMutableMap.toList(tickers))
            Log.wtf("SOCKET", "try open socket")
            socketClient.ws(host = HOST) {
                Log.wtf("SOCKET", "socket started")
                while (true) {
                    val rawMessage = incoming.receive() as? Frame.Text
                    val message = rawMessage?.readText().orEmpty()
                    Log.wtf("SOCKET", "message $message")
                    try {
                        val messageArray = json.parseToJsonElement(message).jsonArray
                        val event: String = json.decodeFromString(
                            messageArray.jsonArray[POSITION_EVENT].toString()
                        )
                        val data: String = messageArray.jsonArray[POSITION_DATA].toString()

                        when (event) {
                            EVENT_USER_DATA -> {
                                val request = createRequest(tickers)
                                Log.wtf("SOCKET", "send request $request")
                                send(Frame.Text(request))
                            }

                            EVENT_QUOTATION -> {
                                val qRaw = json.decodeFromString<QuotationRawData>(data)
                                val q = qRaw.mapToViewData()
                                quotationMutableMap[q.ticker] = q
                                send(quotationMutableMap.toList(tickers))
                            }
                        }
                    } catch (e: Exception) {
                        Log.wtf("SOCKET", e.message)
                    }
                }
            }
        }
    }

    private fun createRequest(tickers: List<Ticker>): String {
        return "[$REQUEST_EVENT_QUOTATIONS,${json.encodeToString(tickers.map { it.ticker })}]"
    }

    private fun Map<String, Quotation>.toList(tickers: List<Ticker>): List<Quotation> {
        val outputList = mutableListOf<Quotation>()
        tickers.forEach { ticker -> this[ticker.ticker]?.let { outputList.add(it) } }
        return outputList
    }

    private fun QuotationRawData.mapToViewData() =
        Quotation(
            ticker = ticker!!,
            changePercent = changePercent
        )

    companion object {
        private const val REQUEST_EVENT_QUOTATIONS = "realtimeQuotes"
        private const val EVENT_USER_DATA = "userData"
        private const val EVENT_QUOTATION = "q"
        private const val POSITION_EVENT = 0
        private const val POSITION_DATA = 1
        private const val HOST = "wss.tradernet.com"
    }
}
