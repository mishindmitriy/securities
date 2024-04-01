package mishin.trader.net.test.data.datasource

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.network.rest.ApiParams
import mishin.trader.net.test.data.network.rest.entity.TickersResponse
import mishin.trader.net.test.domain.Ticker

class TickersRemoteDataSource(
    private val httpClient: HttpClient,
    private val json: Json
) {

    suspend fun getTickers(): List<Ticker>? {
        Log.wtf("TICKERS", "request tickets")
        val response = httpClient.get("https://tradernet.ru/api/") {
            parameter("q", json.encodeToString(ApiParams("getTopSecurities")))
        }
        if (response.status.isSuccess()) {
            val body = response.body<String>()
            val list = json.decodeFromString<TickersResponse>(body)
                .tickers
                ?.filterNotNull()
                ?.map { Ticker(it) }
            if (!list.isNullOrEmpty()) {
                Log.wtf("TICKERS", "return tickets $list")
                return list
            }
        }
        return null
    }
}
