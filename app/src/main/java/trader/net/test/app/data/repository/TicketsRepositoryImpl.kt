package trader.net.test.app.data.repository

import android.util.Log
import trader.net.test.app.data.datasource.TickersLocalDataSource
import trader.net.test.app.data.datasource.TickersRemoteDataSource
import trader.net.test.app.domain.Ticker
import trader.net.test.app.domain.TickersRepository

class TicketsRepositoryImpl(
    private val localDataSource: TickersLocalDataSource,
    private val remoteDataStore: TickersRemoteDataSource
) : TickersRepository {

    override suspend fun getTickers(): List<Ticker> {
        try {
            val remoteData = remoteDataStore.getTickers()
            if (!remoteData.isNullOrEmpty()) return remoteData
        } catch (e: Exception) {
            Log.d(TAG, e.message.orEmpty())
        }
        return localDataSource.getTickers()
    }

    companion object {
        private const val TAG = "TICKETS"
    }
}
