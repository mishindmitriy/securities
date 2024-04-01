package mishin.trader.net.test.di

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.datasource.TickersLocalDataSource
import mishin.trader.net.test.data.datasource.TickersRemoteDataSource
import mishin.trader.net.test.data.repository.QuotationsRepositoryImpl
import mishin.trader.net.test.data.repository.TicketsRepositoryImpl
import mishin.trader.net.test.domain.QuotationsRepository
import mishin.trader.net.test.domain.TickersRepository
import mishin.trader.net.test.presentation.QuotationsViewModel
import org.koin.android.BuildConfig
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DISPATCHER_IO = "dispatcher_io"

val networkModule = module {
    single<CoroutineDispatcher>(named(DISPATCHER_IO)) { Dispatchers.IO }

    single {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    single {
        HttpClient(CIO) {
            install(WebSockets)
            install(Logging) {
                logger = CustomAndroidHttpLogger
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }
            developmentMode = BuildConfig.DEBUG
            install(ContentNegotiation) { get() as Json } //todo replace with kotlin serialisation?
            install(HttpTimeout) {
                requestTimeoutMillis = 2000
            }
        }
    }
}

val dataModule = module {
    single { TickersLocalDataSource() }
    single { TickersRemoteDataSource(get(), get()) }
    single<TickersRepository> { TicketsRepositoryImpl(get(), get()) }
    single<QuotationsRepository> { QuotationsRepositoryImpl(get(), get()) }
}

val presentationModule = module {
    viewModel { QuotationsViewModel(get(), get(), get(named(DISPATCHER_IO))) }
}

private object CustomAndroidHttpLogger : Logger {
    private const val LOG_TAG = "CustomAndroidHttpLogger"

    override fun log(message: String) {
        Log.i(LOG_TAG, message)
    }
}
