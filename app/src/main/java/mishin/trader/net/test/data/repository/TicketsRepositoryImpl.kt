package mishin.trader.net.test.data.repository

import mishin.trader.net.test.data.datasource.TickersLocalDataSource
import mishin.trader.net.test.data.datasource.TickersRemoteDataSource
import mishin.trader.net.test.domain.Ticker
import mishin.trader.net.test.domain.TickersRepository

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
