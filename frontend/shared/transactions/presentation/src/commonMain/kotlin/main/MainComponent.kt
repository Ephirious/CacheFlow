package main

import com.arkivanov.decompose.ComponentContext

@JsExport
interface MainComponent : ComponentContext {

//    @JsName("state")
//    val jsState: JsValue<MainState>

//    @Suppress("unused")
//    fun intent(intent: MainIntent)
}

class RealMainComponent(
    componentCtx: ComponentContext,
//    container: () -> MainContainer,
) : MainComponent, ComponentContext by componentCtx {
//    Store<MainState, MainIntent, Nothing> by componentCtx.retainedStore(factory = container)

//    @OptIn(InternalFlowMVIAPI::class)
//    override val jsState: JsValue<MainState> by lazy {
//        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
//    }
}