package trader.net.test.app.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import trader.net.test.app.R
import trader.net.test.app.databinding.ItemQuotationBinding

class QuotationItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_quotation, parent, false)
) {
    private val binding: ItemQuotationBinding = ItemQuotationBinding.bind(itemView)

    fun update(q: QuotationViewData) {
        binding.ticker.text = q.ticker
        binding.name.text = q.name
        binding.logo.load(q.logoUrl)
        binding.priceAndChange.text = q.priceChange
        binding.changePercent.setTextColor(q.percentColor)
        binding.changePercent.text = q.percentChange
    }
}
