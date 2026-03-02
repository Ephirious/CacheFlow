package root


import interopTest.InteropTestComponent
import root.RootComponent.Config
import utils.presentation.DefaultStack
import kotlinx.serialization.Serializable as Serializable



interface RootComponent : DefaultStack<Config, RootChild> {
    @Serializable
    sealed interface Config {
        @Serializable
        object InteropTest : Config
    }

}

@JsExport
sealed class RootChild {
    class InteropTestChild(val component: InteropTestComponent) : RootChild()
}