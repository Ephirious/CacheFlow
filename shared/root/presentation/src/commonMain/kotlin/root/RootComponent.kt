package root


import interopTest.InteropTestComponent
import utils.presentation.DefaultStack
import kotlinx.serialization.Serializable as Serializable



@JsExport
interface RootComponent : DefaultStack<RootConfig, RootChild> {

}

@Serializable
sealed interface RootConfig {
    @Serializable
    object InteropTest : RootConfig
}

@JsExport
sealed class RootChild {
    class InteropTestChild(val component: InteropTestComponent) : RootChild()
}