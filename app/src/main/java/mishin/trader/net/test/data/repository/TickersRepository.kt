package mishin.trader.net.test.data.repository

import mishin.trader.net.test.domain.Ticker

interface TickersRepository {
    suspend fun getTickers(): List<Ticker>
}
