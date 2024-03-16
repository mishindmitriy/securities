package mishin.trader.net.test.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.network.rest.entity.Ticker
import mishin.trader.net.test.domain.Quotation
import mishin.trader.net.test.domain.QuotationsRepository
import java.util.Collections

class QuotationsRepositoryImpl(
    private val json: Json
) : QuotationsRepository {
    private val quotationMutableMap = Collections.synchronizedMap(hashMapOf<String, Quotation>())
    override suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>> {
        return channelFlow {
            val socketClient = HttpClient(OkHttp) {
                install(WebSockets) {
                    pingInterval = 50_000
                }
            }
            if (quotationMutableMap.isEmpty()) {
                tickers.forEach {
                    quotationMutableMap[it.ticker] = Quotation(it.ticker)
                }
            } else {
                send(quotationMutableMap.values.sortedBy { it.ticker })
            }

            try {
                socketClient.ws(
                    host = "wss.tradernet.ru"
                ) {
                    val request = "[\"quotes\",\"${
                        json.encodeToString(tickers.map { it.ticker })
                    }\"]"
                    send(request)
                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text
                        val message = othersMessage?.readText()
                        println("SOCKET MESSAGE$message")
                    }
                }
            } catch (e: Exception) {
                socketClient.close()
            }

            // TODO: subscribe socket, merge data to hashmap and update
            awaitClose {
                socketClient.close()
                //quotationMutableMap.clear()
                // TODO: close socket
            }
        }
    }
}
