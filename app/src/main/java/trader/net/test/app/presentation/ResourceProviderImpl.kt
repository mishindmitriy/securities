package trader.net.test.app.presentation

import android.content.Context
import trader.net.test.app.domain.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(id: Int) = context.getString(id)
}
