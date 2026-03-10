package more

import com.arkivanov.decompose.ComponentContext

@JsExport
interface MoreComponent : ComponentContext {

//    @JsName("state")
//    val jsState: JsValue<MoreState>

//    @Suppress("unused")
//    fun intent(intent: MoreIntent)
}

class RealMoreComponent(
    componentCtx: ComponentContext,
//    container: () -> MoreContainer,
) : MoreComponent, ComponentContext by componentCtx {
//    Store<MoreState, MoreIntent, Nothing> by componentCtx.retainedStore(factory = container)

//    @OptIn(InternalFlowMVIAPI::class)
//    override val jsState: JsValue<MoreState> by lazy {
//        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
//    }
}