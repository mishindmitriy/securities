package mishin.trader.net.test.domain

interface TickersRepository {
    suspend fun getTickers(): List<Ticker>
}
