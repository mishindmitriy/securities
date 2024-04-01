package trader.net.test.app.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import trader.net.test.app.domain.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(stringRes: Int) = context.getString(stringRes)
    override fun getColor(colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }
}
