package mishin.trader.net.test.data.repository

interface TickersRepository {
    fun getLocalTickers(): List<String>
    suspend fun getRemoteTickers(): List<String>
}