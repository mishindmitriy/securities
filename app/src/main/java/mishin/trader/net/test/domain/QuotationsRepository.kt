package mishin.trader.net.test.domain

import kotlinx.coroutines.flow.Flow

interface QuotationsRepository {
    suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>>
}
