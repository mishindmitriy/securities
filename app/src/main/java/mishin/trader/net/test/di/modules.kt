package mishin.trader.net.test.di

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.json.Json
import mishin.trader.net.test.data.repository.TickersRepository
import mishin.trader.net.test.data.repository.TicketsRepositoryImpl
import mishin.trader.net.test.domain.QuotationsUseCase
import mishin.trader.net.test.domain.QuotationsUseCaseImpl
import mishin.trader.net.test.presentation.QuotationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single<TickersRepository> { TicketsRepositoryImpl(get(), get()) }
}

val useCasesModule = module {
    single<QuotationsUseCase> { QuotationsUseCaseImpl(get()) }
}

val apiModule = module {
    single { Json { encodeDefaults = true } }

    single {
        HttpClient(Android)
        {
            install(Logging) {
                logger = CustomAndroidHttpLogger
                level = LogLevel.ALL
            }
            install(ContentNegotiation) { get() }
            expectSuccess = true
        }
    }
}

val viewModelsModule = module {
    viewModel { QuotationsViewModel(get()) }
}

private object CustomAndroidHttpLogger : Logger {
    private const val logTag = "CustomAndroidHttpLogger"

    override fun log(message: String) {
        Log.i(logTag, message)
    }
}