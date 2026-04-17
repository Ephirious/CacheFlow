package utils.presentation

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperOwner
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

inline fun InstanceKeeperOwner.launchPersistentCoroutine(
    key: String,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    instanceKeeper.getOrCreate(key = key) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        scope.launch {
            block()
        }
        object : InstanceKeeper.Instance {
            override fun onDestroy() {
                scope.cancel()
            }
        }
    }
}