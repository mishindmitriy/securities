package mishin.trader.net.test.data.network.entity

class QuotationRemoteData(
    //@SerializedName("c")
    val ticker: String
) {
    var name: String? = null

    //Изменение в процентах относительно цены закрытия предыдущей торговой сессии
    // @SerializedName("pcp")
    var changePercent: Double? = null

    // @SerializedName("ltr")
    var lastTradeExchange: String? = null //Биржа последней сделки

    // @SerializedName("ltp")
    var lastTradePrice: Double? = null //Цена последней сделки

    //Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии
    // @SerializedName("chg")
    var change: Double? = null

    //  @SerializedName("min_step")
    var minStep: Double? = null
}