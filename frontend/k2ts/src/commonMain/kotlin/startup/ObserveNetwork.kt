package startup

import kotlinx.browser.window
import kotlinx.coroutines.launch
import sync.repositories.SyncManager

fun observeNetwork(
    syncManager: SyncManager
) {
    window.addEventListener(
        "online",
        callback = {
            syncManager.scope.launch {
                syncManager.requestSync()
            }
        }
    )

}