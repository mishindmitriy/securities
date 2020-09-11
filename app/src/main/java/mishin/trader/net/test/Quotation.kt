package mishin.trader.net.test

import com.google.gson.annotations.SerializedName

class Quotation(
    @SerializedName("c") internal val ticker: String
) {
    internal var name: String? = null

    //Изменение в процентах относительно цены закрытия предыдущей торговой сессии
    @SerializedName("pcp")
    internal var changePercent: Double? = null

    @SerializedName("ltr")
    internal var lastTradeExchange: String? = null //Биржа последней сделки

    @SerializedName("ltp")
    internal var lastTradePrice: Double? = null //Цена последней сделки

    //Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии
    @SerializedName("chg")
    internal var change: Double? = null

    @SerializedName("min_step")
    internal var minStep: Double? = null
}