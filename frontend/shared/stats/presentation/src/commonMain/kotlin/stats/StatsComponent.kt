package stats

import com.arkivanov.decompose.ComponentContext
import kotlin.js.JsExport

@JsExport
interface StatsComponent : ComponentContext {

//    @JsName("state")
//    val jsState: JsValue<StatsState>

//    @Suppress("unused")
//    fun intent(intent: StatsIntent)
}

class RealStatsComponent(
    componentCtx: ComponentContext,
//    container: () -> StatsContainer,
) : StatsComponent, ComponentContext by componentCtx {
//    Store<StatsState, StatsIntent, Nothing> by componentCtx.retainedStore(factory = container)

//    @OptIn(InternalFlowMVIAPI::class)
//    override val jsState: JsValue<StatsState> by lazy {
//        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
//    }
}