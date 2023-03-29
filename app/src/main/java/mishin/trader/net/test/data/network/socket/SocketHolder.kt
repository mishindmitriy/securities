package mishin.trader.net.test.data.network.socket

import android.util.Log
import io.socket.client.IO
import kotlinx.coroutines.channels.Channel

class SocketHolder(host: String) {
    private val socket = IO.socket(host)
    private val channelsMap: HashMap<String, Channel<Any>> = hashMapOf()

    init {
        socket.on("ping") { Log.d("tickers", "ping") }
        socket.on("pong") { Log.d("tickers", "pong") }
        /* socket.on(Socket.EVENT_CONNECT) {
             socket.on("q") { args ->

                 val jsonObj: JSONObject = args[0] as JSONObject
                 val jsonArray = jsonObj.getJSONArray("q")
                 Log.d("tickers", jsonArray.toString())
                 *//*emitter.onNext(
                    gson.fromJson(
                        jsonArray.toString(),
                        Array<Quotation>::class.java
                    )
                )*//*
            }
            //socket.emit("realtimeQuotes", JSONArray(tickers))
        }*/
    }

    fun channelForEvent(event: String): Channel<Any> {
        val channel = channelsMap[event]
        return if (channel == null) {
            val newChannel = Channel<Any>()
            channelsMap[event] = newChannel
            socket.on(event) {
                if (it.isNotEmpty()) {

                }
                newChannel.trySend(it)
            }
            newChannel
        } else {
            channel
        }
    }

    fun emit(command: String, argument: Any) {
        socket.emit(command, argument)
    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun close() {
        socket.close()
    }
}