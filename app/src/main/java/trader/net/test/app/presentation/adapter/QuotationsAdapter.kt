package trader.net.test.app.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import trader.net.test.app.presentation.QuotationViewData

class QuotationsAdapter :
    ListAdapter<QuotationViewData, QuotationItemViewHolder>(QuotationsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationItemViewHolder {
        return QuotationItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: QuotationItemViewHolder, position: Int) {
        holder.update(getItem(position))
    }
}
