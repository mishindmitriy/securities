package mishin.trader.net.test.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
            val outputList = mutableListOf<Quotation>()
            tickers.forEach { ticker ->
                quotationMutableMap[ticker.ticker]?.let { outputList.add(it) }
            }
            send(outputList)

            var session: ClientWebSocketSession? = null
            try {
                Log.wtf("SOCKET", "try open socket")
                //todo вынести хост в gradle
                socketClient.ws(host = "wss.tradernet.com") {
                    session = this
                    Log.wtf("SOCKET", "socket started")
                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text
                        val message = othersMessage?.readText()
                        Log.wtf("SOCKET", "message $message")

                        if (message?.contains("userData") == true) {
                            //todo складывать запросы в очередь и отсылать когда получили ответ?
                            val request =
                                "[\"realtimeQuotes\",${json.encodeToString(tickers.map { it.ticker })}]"
                            Log.wtf("SOCKET", "send request $request")
                            send(Frame.Text(request))
                        } else {
                            //todo смапить данные
                            //могут ли данные прилетать в разных потоках? что будет аффектить хешмапу конкаренси
                        }
                    }
                    // TODO: close socket
                }
            } finally {
                Log.wtf("SOCKET", "close")
                session?.let { it.launch { it.close() } }
            }
        }
    }
}
