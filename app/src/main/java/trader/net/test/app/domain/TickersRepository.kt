package trader.net.test.app.domain

interface TickersRepository {
    suspend fun getTickers(): List<Ticker>
}
