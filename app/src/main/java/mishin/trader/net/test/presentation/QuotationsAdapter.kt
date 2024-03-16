package mishin.trader.net.test.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mishin.trader.net.test.data.network.rest.entity.QuotationRestData

class QuotationsAdapter : RecyclerView.Adapter<QuotationItemViewHolder>() {
    private var quotationsArray: Array<QuotationRestData>? = null

    //map with ticker keys for updates
    private var updatedQuotationsMap = HashMap<String, QuotationRestData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationItemViewHolder {
        return QuotationItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: QuotationItemViewHolder, position: Int) {
        val quotation = quotationsArray!![position]
        val updatedQuotation = updatedQuotationsMap[quotation.ticker]
        if (updatedQuotation != null) {
            if (updatedQuotation.name != null) quotation.name = updatedQuotation.name
            if (updatedQuotation.lastTradeExchange != null) quotation.lastTradeExchange =
                updatedQuotation.lastTradeExchange
        }
        holder.update(quotation, updatedQuotation)
        if (updatedQuotation != null) {
            updatedQuotationsMap.remove(quotation.ticker)
            //update current object with new fields
            if (updatedQuotation.change != null) quotation.change = updatedQuotation.change
            if (updatedQuotation.lastTradePrice != null) quotation.lastTradePrice =
                updatedQuotation.lastTradePrice
            if (updatedQuotation.lastTradeExchange != null) quotation.lastTradeExchange =
                updatedQuotation.lastTradeExchange
            if (updatedQuotation.changePercent != null) quotation.changePercent =
                updatedQuotation.changePercent
        }
    }

    override fun getItemCount(): Int {
        return quotationsArray?.size ?: 0
    }

    fun initQuotations(data: Array<QuotationRestData>) {
        quotationsArray = data
        notifyDataSetChanged()
    }

    fun updateFields(newData: Array<QuotationRestData>) {
        newData.forEach { quotation -> updatedQuotationsMap[quotation.ticker] = quotation }
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return itemCount
            }

            override fun getNewListSize(): Int {
                return itemCount
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return !updatedQuotationsMap.containsKey(quotationsArray!![oldItemPosition].ticker)
            }
        })
        result.dispatchUpdatesTo(this)
    }

    fun isEmpty(): Boolean {
        return quotationsArray == null
    }
}