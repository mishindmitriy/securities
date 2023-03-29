package mishin.trader.net.test.domain

interface QuotationsUseCase {
    suspend fun getTickers(): List<String>
}