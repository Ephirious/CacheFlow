package startup

import kotlinx.browser.window
import sync.repositories.SyncManager

fun observeNetwork(
    syncManager: SyncManager
) {

    window.addEventListener(
        "online",
        callback = {
            syncManager.requestSync()
        }
    )
}