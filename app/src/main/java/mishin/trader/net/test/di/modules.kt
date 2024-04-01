package mishin.trader.net.test.di

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import mishin.trader.net.test.BuildConfig
import mishin.trader.net.test.data.datasource.TickersLocalDataSource
import mishin.trader.net.test.data.datasource.TickersRemoteDataSource
import mishin.trader.net.test.data.repository.QuotationsRepositoryImpl
import mishin.trader.net.test.data.repository.TicketsRepositoryImpl
import mishin.trader.net.test.domain.QuotationsRepository
import mishin.trader.net.test.domain.TickersRepository
import mishin.trader.net.test.presentation.QuotationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {
    single {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    single {
        HttpClient(Android) {
            install(Logging) {
                logger = CustomAndroidHttpLogger
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }
            install(ContentNegotiation) { get() as Json }
        }
    }
}

val dataModule = module {
    single { TickersLocalDataSource() }
    single { TickersRemoteDataSource(get(), get()) }
    single<TickersRepository> { TicketsRepositoryImpl(get(), get()) }
    single<QuotationsRepository> { QuotationsRepositoryImpl(get()) }
}

val presentationModule = module {
    viewModel { QuotationsViewModel(get(), get()) }
}

private object CustomAndroidHttpLogger : Logger {
    private const val LOG_TAG = "CustomAndroidHttpLogger"

    override fun log(message: String) {
        Log.i(LOG_TAG, message)
    }
}
