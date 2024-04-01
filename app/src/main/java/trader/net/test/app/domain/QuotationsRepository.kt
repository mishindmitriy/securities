package trader.net.test.app.domain

import kotlinx.coroutines.flow.Flow

interface QuotationsRepository {
    suspend fun getQuotations(tickers: List<Ticker>): Flow<List<Quotation>>
}
