package mishin.trader.net.test.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.domain.Quotation
import mishin.trader.net.test.domain.QuotationsRepository
import mishin.trader.net.test.domain.Ticker
import java.util.Collections

class QuotationsRepositoryImpl(
    private val json: Json
) : QuotationsRepository {

    private val socketClient = HttpClient(CIO) {
        install(WebSockets) {
            Log.wtf("SOCKET", "install socket client")
            //pingInterval = 50_000
        }
    }
    private val quotationMutableMap = Collections.synchronizedMap(hashMapOf<String, Quotation>())
    override suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>> {
        return channelFlow {
            if (quotationMutableMap.isEmpty()) {
                tickers.forEach {
                    quotationMutableMap[it.ticker] = Quotation(it.ticker)
                }
            } else {
                send(quotationMutableMap.values.sortedBy { it.ticker })
            }

            try {
                Log.wtf("SOCKET", "try open socket")
                socketClient.ws(host = "wss.tradernet.com") {
                    Log.wtf("SOCKET", "socket started")
                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text
                        val message = othersMessage?.readText()
                        Log.wtf("SOCKET", "message $message")
                        if (message?.contains("userData") == true) {
                            val request = "[\"realtimeQuotes\",${
                                json.encodeToString(tickers.map { it.ticker })
                            }]"
                            Log.wtf("SOCKET", "send request $request")
                            send(Frame.Text(request))
                        }
                    }
                    // TODO: close socket
                }
            } catch (e: Exception) {
                Log.wtf("SOCKET", "exception $e")
            }

            // TODO: subscribe socket, merge data to hashmap and update
            awaitClose {
                //quotationMutableMap.clear()
                // TODO: close socket
            }
        }
    }
}
