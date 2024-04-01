package trader.net.test.app.presentation

import androidx.annotation.ColorInt


data class QuotationViewData(
    val ticker: String,
    val logoUrl: String,
    val name: String,
    val priceChange: String,
    @ColorInt
    val percentColor: Int,
    val percentChange: String
)
