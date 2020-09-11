package mishin.trader.net.test.network

import com.google.gson.Gson
import io.reactivex.Single
import mishin.trader.net.test.Quotation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TraderNetRestService {
    @GET("securities/export?params=ltr+ltp+pcp+chg+name")
    fun getQuotations(@Query("tickers", encoded = true) tickers: String): Single<Array<Quotation>>

    @GET("api")
    fun getTopSecurities(
        @Query("q")
        q: String =
            Gson().toJson(
                ApiParams("getTopSecurities", RequestParams("stocks", "usa"))
            )
    ): Single<TopSecuritiesResponse>

    companion object Factory {
        fun create(): TraderNetRestService {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder().baseUrl("https://tradernet.ru/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TraderNetRestService::class.java)
        }
    }
}