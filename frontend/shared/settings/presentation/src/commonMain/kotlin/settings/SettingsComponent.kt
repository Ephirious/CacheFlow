package settings

import com.arkivanov.decompose.ComponentContext
import kotlin.js.JsExport

@JsExport
interface SettingsComponent : ComponentContext {

//    @JsName("state")
//    val jsState: JsValue<MoreState>

//    @Suppress("unused")
//    fun intent(intent: MoreIntent)
}

class RealSettingsComponent(
    componentCtx: ComponentContext,
//    container: () -> MoreContainer,
) : SettingsComponent, ComponentContext by componentCtx {
//    Store<MoreState, MoreIntent, Nothing> by componentCtx.retainedStore(factory = container)

//    @OptIn(InternalFlowMVIAPI::class)
//    override val jsState: JsValue<MoreState> by lazy {
//        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
//    }
}