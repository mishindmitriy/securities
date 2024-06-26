package trader.net.test.app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import trader.net.test.app.API_URL
import trader.net.test.app.data.network.tickers.GetTickersRequest
import trader.net.test.app.data.network.tickers.TickersResponse
import trader.net.test.app.domain.Ticker

class TickersRemoteDataSource(
    private val httpClient: HttpClient,
    private val json: Json
) {

    suspend fun getTickers(): List<Ticker>? {
        val response = httpClient.get(API_URL) {
            parameter(REQUEST_PARAMETER_NAME, json.encodeToString(GetTickersRequest()))
        }
        if (response.status.isSuccess()) {
            val body = response.body<String>()
            val list = json.decodeFromString<TickersResponse>(body)
                .tickers
                ?.filterNotNull()
                ?.map { Ticker(it) }
            if (!list.isNullOrEmpty()) {
                return list
            }
        }
        return null
    }

    companion object {
        private const val REQUEST_PARAMETER_NAME = "q"
    }
}
