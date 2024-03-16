package mishin.trader.net.test.data.repository

import mishin.trader.net.test.data.network.rest.entity.Ticker

interface TickersRepository {
    suspend fun getTickers(): List<Ticker>
}