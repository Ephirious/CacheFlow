package sync.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SyncLocalDataSource(
    val settings: Settings
) {
    fun setLastTimeSync(lastSyncTimestamp: String) {
        settings[LAST_SYNC_TIMESTAMP_KEY] = lastSyncTimestamp
    }

    fun getLastTimeSync(): String =
        settings[LAST_SYNC_TIMESTAMP_KEY, DEFAULT_SYNC_TIMESTAMP]


    companion object {
        const val LAST_SYNC_TIMESTAMP_KEY = "lastSyncTimestampKey"

        const val DEFAULT_SYNC_TIMESTAMP = "1970-01-01T00:00:00Z"
    }
}
