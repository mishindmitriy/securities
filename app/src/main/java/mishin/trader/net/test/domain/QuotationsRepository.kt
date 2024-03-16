package mishin.trader.net.test.domain

import kotlinx.coroutines.flow.Flow
import mishin.trader.net.test.data.network.rest.entity.Ticker

interface QuotationsRepository {
    suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>>
}
