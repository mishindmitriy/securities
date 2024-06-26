package trader.net.test.app.data.network.quotations

import kotlinx.serialization.SerialName

/**
 * @param ticker - Тикер
 * @param changePercent - Изменение в процентах относительно цены закрытия предыдущей торговой
 * сессии
 * @param lastTradeExchange - Биржа последней сделки
 * @param name - Название бумаги
 * @param lastTradePrice - Цена последней сделки
 * @param change - Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей
 * торговой сессии
 * @param minStep - Шаг округления
 */
@kotlinx.serialization.Serializable
data class QuotationRawData(
    @SerialName("c")
    var ticker: String? = null,
    @SerialName("pcp")
    var changePercent: Double? = null,
    @SerialName("ltr")
    var lastTradeExchange: String? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("ltp")
    var lastTradePrice: Double? = null,
    @SerialName("chg")
    var change: Double? = null,
    @SerialName("min_step")
    var minStep: Double? = null
)
