package mishin.trader.net.test.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.CachePolicy
import coil.request.Disposable
import coil.request.ImageRequest
import mishin.trader.net.test.R
import mishin.trader.net.test.data.network.rest.entity.QuotationRestData
import mishin.trader.net.test.databinding.ItemQuotationBinding
import java.text.DecimalFormat

class QuotationItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_quotation, parent, false)
) {
    private val binding: ItemQuotationBinding = ItemQuotationBinding.bind(itemView)
    private val imageLoader = Coil.imageLoader(parent.context)
    private var disposable: Disposable? = null
    private var backgroundColorRunnable: Runnable? = null
    private val decimalFormat = DecimalFormat("###.#####")

    fun update(q: QuotationRestData, qUpd: QuotationRestData?) {
        showStaticFields(q)

        val changePercent: Double? =
            if (qUpd?.changePercent != null) qUpd.changePercent else q.changePercent

        val changeDirection: ChangeDirection = when {
            qUpd?.change == null || q.change == null -> {
                ChangeDirection.NONE
            }

            q.change!! > qUpd.change!! -> {
                ChangeDirection.NEGATIVE
            }
            else -> {
                ChangeDirection.POSITIVE
            }
        }
        showChangingPercent(changePercent, changeDirection)

        val price: Double? =
            if (qUpd?.lastTradePrice != null) qUpd.lastTradePrice else q.lastTradePrice
        val change: Double? =
            if (qUpd?.change != null) qUpd.change else q.change
        showPrice(price, change)
    }

    private fun showPrice(price: Double?, change: Double?) {
        if (price == null) {
            binding.priceAndChange.text = null
        } else {
            val changeString: String = if (change == null) ""
            else (if (change > 0.0) "+${decimalFormat.format(change)}" else decimalFormat.format(
                change
            ))
            binding.priceAndChange.text = "$price ($changeString)"
        }
    }

    enum class ChangeDirection {
        POSITIVE, NEGATIVE, NONE
    }

    private fun showChangingPercent(
        changePercent: Double?,
        changeDirection: ChangeDirection = ChangeDirection.NONE
    ) {
        binding.changePercent.background = null
        binding.changePercent.removeCallbacks(backgroundColorRunnable)
        if (changePercent == null) {
            binding.changePercent.text = null
            return
        }
        val prefix: String
        @ColorInt val color: Int
        when {
            changePercent > 0 -> {
                prefix = "+"
                color = ContextCompat.getColor(itemView.context, R.color.green)
            }
            changePercent < 0 -> {
                prefix = ""
                color = ContextCompat.getColor(itemView.context, R.color.red)
            }
            else -> {
                prefix = ""
                color = ContextCompat.getColor(itemView.context, R.color.black_opacity_90)
            }
        }
        binding.changePercent.setTextColor(color)
        binding.changePercent.text = "$prefix${decimalFormat.format(changePercent)}%"

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
    }

    private fun showStaticFields(quotation: QuotationRestData) {
        binding.ticker.text = quotation.ticker
        if (quotation.name == null) {
            binding.name.text = ""
        } else if (quotation.lastTradeExchange == null) {
            binding.name.text = quotation.name
        } else {
            binding.name.text = "${quotation.lastTradeExchange} | ${quotation.name}"
        }
        loadLogo(quotation.ticker)
    }

    private fun loadLogo(ticker: String) {
        binding.logo.visibility = View.GONE
        val request = ImageRequest.Builder(itemView.context)
            .data("https://tradernet.ru/logos/get-logo-by-ticker?ticker=" + ticker.lowercase())
            .target { drawable ->
                binding.logo.visibility = VISIBLE
                binding.logo.setImageDrawable(drawable)
            }
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
        disposable?.dispose()
        disposable = imageLoader.enqueue(request)
    }
}
