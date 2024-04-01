package trader.net.test.app.domain

interface ResourceProvider {
    fun getString(id: Int): String
}
