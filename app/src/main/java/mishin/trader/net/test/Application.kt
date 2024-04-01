package mishin.trader.net.test

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.DebugLogger
import mishin.trader.net.test.di.dataModule
import mishin.trader.net.test.di.networkModule
import mishin.trader.net.test.di.presentationModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application(), ImageLoaderFactory {

    private val modules = listOf(
        networkModule,
        dataModule,
        presentationModule
    )

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@App)
            modules(modules)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(context = this)
            .newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .also {
                if (BuildConfig.DEBUG) it.logger(DebugLogger())
            }
            .build()
    }
}
