import main.mvi.MainContainer
import org.koin.dsl.module

val transactionsPresentationModule = module {
    factory<() -> MainContainer> {
        { MainContainer() }
    }
}