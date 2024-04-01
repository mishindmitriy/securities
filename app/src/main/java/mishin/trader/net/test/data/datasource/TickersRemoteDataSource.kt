package mishin.trader.net.test.data.datasource

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

class TickersRemoteDataSource(
    private val httpClient: HttpClient,
    private val json: Json
) {

    suspend fun getTickers(): List<Ticker>? {
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
        return null
    }
}
