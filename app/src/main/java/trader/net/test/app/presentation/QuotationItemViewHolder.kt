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

    fun updatePercentage() {

    }

    /*enum class ChangeDirection {
        POSITIVE, NEGATIVE, NONE
    }



        when (changeDirection) {
            ChangeDirection.POSITIVE -> {
                binding.changePercent.setBackgroundResource(R.drawable.background_green)
                binding.changePercent.setTextColor(Color.WHITE)
                backgroundColorRunnable = Runnable {
                    binding.changePercent.background = null
                    binding.changePercent.setTextColor(color)
                }
                binding.changePercent.postDelayed(backgroundColorRunnable, 500)
            }

            ChangeDirection.NEGATIVE -> {
                binding.changePercent.setBackgroundResource(R.drawable.background_red)
                binding.changePercent.setTextColor(Color.WHITE)
                backgroundColorRunnable = Runnable {
                    binding.changePercent.background = null
                    binding.changePercent.setTextColor(color)
                }
                binding.changePercent.postDelayed(backgroundColorRunnable, 500)
            }

            ChangeDirection.NONE -> {

            }
        }
    }*/

}
