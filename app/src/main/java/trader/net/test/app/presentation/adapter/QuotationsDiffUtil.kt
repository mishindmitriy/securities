package trader.net.test.app.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import trader.net.test.app.presentation.QuotationViewData

class QuotationsDiffUtil : DiffUtil.ItemCallback<QuotationViewData>() {
    override fun areItemsTheSame(oldItem: QuotationViewData, newItem: QuotationViewData): Boolean {
        return oldItem.ticker == newItem.ticker
    }

    override fun areContentsTheSame(
        oldItem: QuotationViewData,
        newItem: QuotationViewData
    ): Boolean {
        return oldItem == newItem
    }
}
