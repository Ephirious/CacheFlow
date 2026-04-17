package utils.presentation.flowMVI

import pro.respawn.flowmvi.api.*
import pro.respawn.flowmvi.dsl.StoreBuilder
import pro.respawn.flowmvi.dsl.StorePluginBuilder
import pro.respawn.flowmvi.dsl.plugin
import pro.respawn.flowmvi.plugins.Reduce
import pro.respawn.flowmvi.plugins.ReducePluginName

@FlowMVIDSL
inline fun <S : MVIState, I : MVIIntent, A : MVIAction> StoreBuilder<S, I, A>.customReduce(
    consume: Boolean = true,
    name: String = ReducePluginName + "Custom",
    crossinline reduce: Reduce<S, I, A>,
): Unit = install(customReducePlugin(consume, name, reduce))

@FlowMVIDSL
inline fun <S : MVIState, I : MVIIntent, A : MVIAction> customReducePlugin(
    consume: Boolean = true,
    name: String = ReducePluginName + "Custom",
    crossinline reduce: Reduce<S, I, A>,
): StorePlugin<S, I, A> = plugin {
    this.name = name
    customOnIntent {
        reduce(it)
        it.takeUnless { consume }
    }
}

@FlowMVIDSL
fun <S : MVIState, I : MVIIntent, A : MVIAction> StorePluginBuilder<S, I, A>.customOnIntent(
    block: suspend PipelineContext<S, I, A>.(intent: I) -> I?
) {
    onIntent { intent ->
        try {
            block(intent)
        } catch (e: Throwable) {
            if (e is Exception) throw e
            throw RuntimeException(e)
        }
    }
}

