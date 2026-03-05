package root


import interopTest.InteropTestComponent
import utils.presentation.DefaultStack
import kotlinx.serialization.Serializable as Serializable


@JsExport
interface RootComponent : DefaultStack<RootConfig, RootChild> {
    fun testPush()
}

@Serializable
sealed interface RootConfig {
    @Serializable
    data class InteropTest(val x: Int) : RootConfig
}

@JsExport
sealed class RootChild {
    class InteropTestChild(val component: InteropTestComponent) : RootChild()
}