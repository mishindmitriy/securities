package mishin.trader.net.test.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.network.rest.ApiParams
import mishin.trader.net.test.data.network.rest.entity.TickersResponse
import mishin.trader.net.test.domain.Ticker
import mishin.trader.net.test.domain.TickersRepository

class TicketsRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : TickersRepository {
    private fun getLocalTickers(): List<Ticker> {
        Log.wtf("Tickers", "get local")
        return "SP500.IDX,AAPL.US,RSTI,GAZP,MRKZ,RUAL,HYDR,MRKS,SBER,FEES,TGKA,VTBR,AN H.US,VICL.US,BURG.US,NBL.US,YETI.US,WSFS.US,NIO.US,DXC.US,MIC.US,HSBC.US,EXPN.EU,GSK.EU,SHP.EU,MAN.EU,DB1.EU,MUV2.EU,TATE.EU,KGF.EU,MGGT.EU,SG GD.EU"
            .split(",")
            .toList()
            .map { Ticker(it) }
    }

    override suspend fun getTickers(): List<Ticker> {
        Log.wtf("Tickers", "request tickets")
        val response = httpClient.get("https://tradernet.ru/api/")
        {
            val str = json.encodeToString(ApiParams("getTopSecurities"))
            parameter("q", str)
        }
        if (response.status.value == 200) {
            try {
                val body = response.body<String>()
                val list = json.decodeFromString<TickersResponse>(body)
                    .tickers
                    ?.filterNotNull()
                    ?.map { Ticker(it) }
                if (!list.isNullOrEmpty()) {
                    Log.wtf("Tickers", "return tickets $list")
                    return list
                }
            } catch (e: Exception) {
                Log.wtf("Tickers", "exception $e")
            }
        }
        return getLocalTickers()
    }
}
