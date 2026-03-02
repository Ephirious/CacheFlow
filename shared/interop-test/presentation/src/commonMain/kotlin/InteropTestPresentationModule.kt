import interopTest.InteropTestContainer
import org.koin.dsl.module

val interopTestPresentationModule = module {
    factory<() -> InteropTestContainer> {
        { InteropTestContainer() }
    }
}