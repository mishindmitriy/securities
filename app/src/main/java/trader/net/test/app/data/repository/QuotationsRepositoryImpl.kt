package trader.net.test.app.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import trader.net.test.app.SOCKET_HOST
import trader.net.test.app.data.network.quotations.QuotationRawData
import trader.net.test.app.domain.Quotation
import trader.net.test.app.domain.QuotationsRepository
import trader.net.test.app.domain.Ticker

class QuotationsRepositoryImpl(
    private val json: Json,
    private val socketClient: HttpClient
) : QuotationsRepository {

    override suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>> {
        val quotationMutableMap: MutableMap<String, Quotation> = hashMapOf()
        tickers.forEach { ticker ->
            quotationMutableMap[ticker.value] = Quotation(ticker = ticker.value, name = "")
        }

        return channelFlow {
            send(quotationMutableMap.toList(tickers))
            socketClient.ws(host = SOCKET_HOST) {
                while (true) {
                    val rawMessage = incoming.receive() as? Frame.Text
                    val message = rawMessage?.readText().orEmpty()
                    try {
                        val messageArray = json.parseToJsonElement(message).jsonArray
                        val event: String = json.decodeFromString(
                            messageArray.jsonArray[POSITION_EVENT].toString()
                        )
                        val data: String = messageArray.jsonArray[POSITION_DATA].toString()

                        when (event) {
                            EVENT_USER_DATA -> {
                                val request = createRequest(tickers)
                                send(Frame.Text(request))
                            }

                            EVENT_QUOTATION -> {
                                val qRaw = json.decodeFromString<QuotationRawData>(data)
                                Log.d(LOG_TAG, "new data $data")
                                val existQ = quotationMutableMap[qRaw.ticker]
                                val updQ = existQ?.mergeQ(qRaw) ?: qRaw.mapToViewData()
                                quotationMutableMap[updQ.ticker] = updQ
                                send(quotationMutableMap.toList(tickers))
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(LOG_TAG, e.message.orEmpty())
                    }
                }
            }
        }
            .debounce(FLOW_DEBOUNCE_MILLIS)
    }

    private fun Quotation.mergeQ(newQ: QuotationRawData): Quotation {
        changeType = Quotation.ChangeType.NONE
        newQ.change?.let { change = it }
        newQ.changePercent?.let { newValue ->
            changeType = when {
                newValue == changePercent -> Quotation.ChangeType.NONE
                newValue > changePercent -> Quotation.ChangeType.POSITIVE
                else -> Quotation.ChangeType.NEGATIVE
            }
            changePercent = newValue
        }
        newQ.lastTradeExchange?.let { lastTradeExchange = it }
        newQ.lastTradePrice?.let { lastTradePrice = it }
        newQ.minStep?.let { minStep = it }
        newQ.name?.let { name = it }
        return this
    }

    private fun createRequest(tickers: List<Ticker>): String {
        val stringTickersList = tickers.map { it.value }
        return String.format(
            REQUEST_PATTERN,
            REQUEST_EVENT_QUOTATIONS,
            json.encodeToString(stringTickersList)
        )
    }

    private fun Map<String, Quotation>.toList(tickers: List<Ticker>): List<Quotation> {
        val outputList = mutableListOf<Quotation>()
        tickers.forEach { ticker -> this[ticker.value]?.let { outputList.add(it) } }
        return outputList
    }

    private fun QuotationRawData.mapToViewData() =
        Quotation(
            //ticker require, exception will skip entity
            ticker = ticker!!,
            changePercent = changePercent ?: 0.0,
            lastTradeExchange = lastTradeExchange ?: "",
            lastTradePrice = lastTradePrice ?: 0.0,
            change = change ?: 0.0,
            name = name.orEmpty(),
            minStep = minStep ?: 0.0
        )

    companion object {
        private const val LOG_TAG = "SOCKET"
        private const val REQUEST_PATTERN = "[\"%s\",%s]"
        private const val REQUEST_EVENT_QUOTATIONS = "realtimeQuotes"
        private const val EVENT_USER_DATA = "userData"
        private const val EVENT_QUOTATION = "q"
        private const val POSITION_EVENT = 0
        private const val POSITION_DATA = 1
        private const val FLOW_DEBOUNCE_MILLIS = 50L
    }
}
