package trader.net.test.app.domain

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes stringRes: Int): String

    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int
}
