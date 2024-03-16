package mishin.trader.net.test.domain

import kotlinx.coroutines.flow.Flow
import mishin.trader.net.test.data.repository.TickersRepository

internal class QuotationsUseCaseImpl(
    private val tickersRepository: TickersRepository,
    private val quotationsRepository: QuotationsRepository
) :
    QuotationsUseCase {
    override suspend fun getQuatations(): Flow<List<Quotation>> {
        return quotationsRepository.getQuotations(tickersRepository.getTickers())
    }
}
