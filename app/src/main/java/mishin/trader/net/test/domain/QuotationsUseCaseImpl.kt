package mishin.trader.net.test.domain

import mishin.trader.net.test.data.repository.TickersRepository

internal class QuotationsUseCaseImpl(private val repository: TickersRepository) :
    QuotationsUseCase {
    override suspend fun getTickers(): List<String> {
        return repository.getRemoteTickers()
    }
}