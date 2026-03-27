package transactions.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class TransactionsLocalDataSource(
    val settings: Settings
) {

    fun setFirstEntrance() {
        settings[FIRST_ENTRANCE_KEY] = true
    }

    fun getFirstEntrance(): Boolean =
        settings[FIRST_ENTRANCE_KEY, false]


    companion object {
        const val FIRST_ENTRANCE_KEY = "firstEntranceKey"
    }
}