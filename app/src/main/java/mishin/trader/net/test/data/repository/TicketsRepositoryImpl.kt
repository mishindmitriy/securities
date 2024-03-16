package mishin.trader.net.test.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.network.rest.ApiParams
import mishin.trader.net.test.data.network.rest.entity.Ticker
import mishin.trader.net.test.data.network.rest.entity.TickersResponse

class TicketsRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : TickersRepository {
    private fun getLocalTickers(): List<Ticker> {
        return "SP500.IDX,AAPL.US,RSTI,GAZP,MRKZ,RUAL,HYDR,MRKS,SBER,FEES,TGKA,VTBR,AN H.US,VICL.US,BURG.US,NBL.US,YETI.US,WSFS.US,NIO.US,DXC.US,MIC.US,HSBC.US,EXPN.EU,GSK.EU,SHP.EU,MAN.EU,DB1.EU,MUV2.EU,TATE.EU,KGF.EU,MGGT.EU,SG GD.EU"
            .split(",")
            .toList()
            .map { Ticker(it) }
    }

    override suspend fun getTickers(): List<Ticker> {
        val response = httpClient.get("https://tradernet.ru/api/")
        {
            val str = json.encodeToString(ApiParams("getTopSecurities"))
            parameter("q", str)
        }
        return if (response.status.value == 200) {
            try {
                response.body<TickersResponse>().tickers.map { Ticker(it) }
            } catch (e: Exception) {
                getLocalTickers()
            }
        } else {
            getLocalTickers()
        }
    }
}
