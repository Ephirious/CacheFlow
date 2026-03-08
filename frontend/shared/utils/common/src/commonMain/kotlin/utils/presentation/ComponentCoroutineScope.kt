package utils.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val ComponentContext.componentCoroutineScope: CoroutineScope
    get() = componentCoroutineScope()


fun ComponentContext.componentCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope {
    val scope = CoroutineScope(context + SupervisorJob() + Dispatchers.Main.immediate)
    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }
    return scope
}