package trader.net.test.app.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class QuotationsAdapter : RecyclerView.Adapter<QuotationItemViewHolder>() {
    private var quotationsArray: List<QuotationViewData> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationItemViewHolder {
        return QuotationItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: QuotationItemViewHolder, position: Int) {
        val quotation = quotationsArray[position]
        /*val updatedQuotation = updatedQuotationsMap[quotation.ticker]
        if (updatedQuotation != null) {
            if (updatedQuotation.name != null) quotation.name = updatedQuotation.name
            if (updatedQuotation.lastTradeExchange != null) quotation.lastTradeExchange =
                updatedQuotation.lastTradeExchange
        }*/
        holder.update(quotation)
        /*if (updatedQuotation != null) {
            updatedQuotationsMap.remove(quotation.ticker)
            //update current object with new fields
            if (updatedQuotation.change != null) quotation.change = updatedQuotation.change
            if (updatedQuotation.lastTradePrice != null) quotation.lastTradePrice =
                updatedQuotation.lastTradePrice
            if (updatedQuotation.lastTradeExchange != null) quotation.lastTradeExchange =
                updatedQuotation.lastTradeExchange
            if (updatedQuotation.changePercent != null) quotation.changePercent =
                updatedQuotation.changePercent
        }*/
    }

    override fun getItemCount(): Int = quotationsArray.size

    fun updateFields(newData: List<QuotationViewData>) {
        //newData.forEach { quotation -> updatedQuotationsMap[quotation.ticker] = quotation }
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return quotationsArray.size
            }

            override fun getNewListSize(): Int {
                return newData.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return quotationsArray[oldItemPosition].ticker == newData[newItemPosition].ticker
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return quotationsArray[oldItemPosition] == newData[newItemPosition]
            }
        })
        result.dispatchUpdatesTo(this)
        quotationsArray = newData
    }
}
