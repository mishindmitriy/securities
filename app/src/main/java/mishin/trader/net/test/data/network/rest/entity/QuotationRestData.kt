package mishin.trader.net.test.data.network.rest.entity

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuotationRestData(
    @SerialName("c")
    var ticker: String,
    @SerialName("name")
    var name: String? = null,

    //Изменение в процентах относительно цены закрытия предыдущей торговой сессии
    @SerialName("pcp")
    var changePercent: Double? = null,

    @SerialName("ltr")
    var lastTradeExchange: String? = null, //Биржа последней сделки

    @SerialName("ltp")
    var lastTradePrice: Double? = null, //Цена последней сделки

    //Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии
    @SerialName("chg")
    var change: Double? = null,

    @SerialName("min_step")
    var minStep: Double? = null
)