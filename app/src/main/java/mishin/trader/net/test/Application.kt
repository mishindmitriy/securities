package mishin.trader.net.test

import android.app.Application
import mishin.trader.net.test.di.apiModule
import mishin.trader.net.test.di.repositoryModule
import mishin.trader.net.test.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val modules = listOf(
        apiModule,
        repositoryModule,
        viewModelsModule
    )

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@App)
            modules(modules)
        }
    }
}
