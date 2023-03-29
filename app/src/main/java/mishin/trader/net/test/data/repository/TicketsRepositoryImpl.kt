package mishin.trader.net.test.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.network.entity.TickersResponse
import mishin.trader.net.test.data.network.rest.ApiParams

class TicketsRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : TickersRepository {
    override fun getLocalTickers(): List<String> {
        return "SP500.IDX,AAPL.US,RSTI,GAZP,MRKZ,RUAL,HYDR,MRKS,SBER,FEES,TGKA,VTBR,AN H.US,VICL.US,BURG.US,NBL.US,YETI.US,WSFS.US,NIO.US,DXC.US,MIC.US,HSBC.US,EXPN.EU,GSK.EU,SHP.EU,MAN.EU,DB1.EU,MUV2.EU,TATE.EU,KGF.EU,MGGT.EU,SG GD.EU"
            .split(",")
            .toList()
    }

    override suspend fun getRemoteTickers(): List<String> {
        return httpClient.get("https://tradernet.ru/api/")
        {
            val str = json.encodeToString(ApiParams("getTopSecurities"))
            parameter("q", str)
        }.body<TickersResponse>().tickers
    }
}