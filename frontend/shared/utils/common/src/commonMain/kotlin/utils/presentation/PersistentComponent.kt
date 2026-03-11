package utils.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.essenty.lifecycle.subscribe

fun LifecycleRegistry.mimicFrom(other: Lifecycle) {
    other.subscribe(
        onCreate = { this.create() },
        onStart = { this.start() },
        onResume = { this.resume() },
        onPause = { this.pause() },
        onStop = { this.stop() }
    )
}


class PersistentComponent<T>(
    private val parentContext: ComponentContext,
    private val key: String,
    private val factory: (ComponentContext) -> T
) {
    val registry = LifecycleRegistry()
    private var instance: T? = null

    fun get(childLifecycle: Lifecycle): T {
        registry.mimicFrom(childLifecycle)
        if (instance == null) {
            val ctx = parentContext.childContext(key, lifecycle = registry)
            instance = factory(ctx)
        }
        return instance!!
    }
}

fun <T> ComponentContext.persistent(
    key: String,
    factory: (ComponentContext) -> T
) =
    PersistentComponent(this, key = key, factory = factory)

fun <T> ComponentContext.persistent(
    factory: (ComponentContext) -> T
) = persistent(factory.hashCode().toString(), factory)
