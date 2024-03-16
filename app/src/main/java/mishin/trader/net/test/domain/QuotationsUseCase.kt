package mishin.trader.net.test.domain

import kotlinx.coroutines.flow.Flow

interface QuotationsUseCase {
    suspend fun getQuatations(): Flow<List<Quotation>>
}
