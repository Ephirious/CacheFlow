package interopSampleFlow

import com.arkivanov.decompose.ComponentContext
import interopSampleFlow.interopSample.InteropSampleComponent
import kotlinx.serialization.Serializable
import utils.interop.JsValue
import utils.presentation.DefaultStack

@JsExport
interface InteropSampleFlowComponent : ComponentContext, DefaultStack<InteropSampleFlowConfig, InteropSampleFlowChild> {

    @JsName("state")
    val jsState: JsValue<InteropSampleFlowState>

    @Suppress("unused")
    fun intent(intent: InteropSampleFlowIntent)

    fun createNewTab()

    fun navigateToTab(tabNum: Int)
}

@Serializable
sealed interface InteropSampleFlowConfig {
    @Serializable
    data class InteropSample(val num: Int) : InteropSampleFlowConfig
}

@JsExport
sealed class InteropSampleFlowChild {
    @Suppress("unused")
    class InteropSampleChild(val component: InteropSampleComponent) : InteropSampleFlowChild()
}


