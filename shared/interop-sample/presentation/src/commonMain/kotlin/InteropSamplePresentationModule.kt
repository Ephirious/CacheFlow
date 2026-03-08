import interopSampleFlow.InteropSampleFlowContainer
import org.koin.dsl.module

val interopSamplePresentationModule = module {
    factory<() -> InteropSampleFlowContainer> {
        { InteropSampleFlowContainer(get(), get()) }
    }
}