package mishin.trader.net.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.socket.client.IO
import io.socket.client.Socket
import mishin.trader.net.test.network.TraderNetRestService
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val restService = TraderNetRestService.create()
    private var dataDisposable: Disposable? = null
    private val quotationsAdapter = QuotationsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)
        supportActionBar?.hide()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = quotationsAdapter

        val fixedTickers = true
        dataDisposable = createBaseObservable(fixedTickers)
            .flatMap { tickers ->
                run {
                    val array = Array(tickers.size, init = { i -> Quotation(tickers[i]) })
                    runOnUiThread { quotationsAdapter.initQuotations(array) }
                    return@flatMap createQuotationsUpdatesObservable(tickers)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { quotations ->
                run {
                    if (quotationsAdapter.isEmpty()) {
                        quotationsAdapter.initQuotations(quotations)
                    } else {
                        quotationsAdapter.updateFields(quotations)
                    }
                    Log.d("tickers", "onNext ${quotations.size}")
                }
            }
    }

    private fun createBaseObservable(fixedTickers: Boolean): Observable<Array<String>> {
        if (fixedTickers)
            return Observable.just(
                arrayOf(
                    "RSTI",
                    "GAZP",
                    "MRKZ",
                    "RUAL",
                    "HYDR",
                    "MRKS",
                    "SBER",
                    "FEES",
                    "TGKA",
                    "VTBR",
                    "ANH.US",
                    "VICL.US",
                    "BURG.US",
                    "NBL.US",
                    "YETI.US",
                    "WSFS.US",
                    "NIO.US",
                    "DXC.US",
                    "MIC.US",
                    "HSBC.US",
                    "EXPN.EU",
                    "GSK.EU",
                    "SHP.EU",
                    "MAN.EU",
                    "DB1.EU",
                    "MUV2.EU",
                    "TATE.EU",
                    "KGF.EU",
                    "MGGT.EU",
                    "SGGD.EU"
                )
            ) else return restService.getTopSecurities()
            .map { body -> return@map body.tickers }
            .toObservable()
    }

    private fun createQuotationsUpdatesObservable(tickers: Array<String>): ObservableSource<Array<Quotation>>? {
        val gson = Gson()
        val tickersString: StringBuilder = StringBuilder()
        tickers.forEachIndexed { index, ticker ->
            run {
                tickersString.append(ticker);
                if (index != (ticker.length - 1)) tickersString.append("+")
            }
        }
        return Observable.create { emitter ->
            run {
                val socket = IO.socket("https://ws3.tradernet.ru/")
                socket.on("ping") { Log.d("tickers", "ping") }
                socket.on("pong") { Log.d("tickers", "pong") }
                socket.on(Socket.EVENT_CONNECT) {
                    socket.on("q") { args ->
                        run {
                            val jsonObj: JSONObject = args[0] as JSONObject
                            val jsonArray = jsonObj.getJSONArray("q")
                            Log.d("tickers", jsonArray.toString())
                            emitter.onNext(
                                gson.fromJson(
                                    jsonArray.toString(),
                                    Array<Quotation>::class.java
                                )
                            )
                        }
                    }
                    socket.emit("sup_updateSecurities2", JSONArray(tickers))
                }
                socket.connect()
                emitter.setCancellable { socket.close() }
            }
        }
    }

    override fun onDestroy() {
        dataDisposable?.dispose()
        super.onDestroy()
    }
}