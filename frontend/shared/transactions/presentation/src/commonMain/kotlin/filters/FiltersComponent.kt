package filters

import com.arkivanov.decompose.ComponentContext
import filters.mvi.FiltersContainer
import filters.mvi.FiltersIntent
import filters.mvi.FiltersState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface FiltersComponent : ComponentContext {

    @JsName("state")
    val jsState: JsValue<FiltersState>

    @Suppress("unused")
    fun intent(intent: FiltersIntent)


}


class RealFiltersComponent(
    componentCtx: ComponentContext,
    container: () -> FiltersContainer,
) : FiltersComponent, ComponentContext by componentCtx,
    Store<FiltersState, FiltersIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<FiltersState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}