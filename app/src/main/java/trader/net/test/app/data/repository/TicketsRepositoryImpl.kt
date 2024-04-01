package trader.net.test.app.data.repository

import trader.net.test.app.data.datasource.TickersLocalDataSource
import trader.net.test.app.data.datasource.TickersRemoteDataSource
import trader.net.test.app.domain.Ticker
import trader.net.test.app.domain.TickersRepository

class TicketsRepositoryImpl(
    private val localDataSource: TickersLocalDataSource,
    private val remoteDataStore: TickersRemoteDataSource
) : TickersRepository {

    override suspend fun getTickers(): List<Ticker> {
        val remoteData = remoteDataStore.getTickers()
        if (!remoteData.isNullOrEmpty()) return remoteData
        return localDataSource.getTickers()
    }
}
