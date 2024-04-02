package trader.net.test.app.presentation

import androidx.annotation.ColorInt
import trader.net.test.app.domain.Quotation


data class QuotationViewData(
    val ticker: String,
    val logoUrl: String,
    val name: String,
    val priceWithChange: String,
    @ColorInt
    val percentColor: Int,
    val percentChange: String,
    val animate: Quotation.ChangeType
)
