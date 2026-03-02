package di

import core.coreModule
import interopTestPresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


// do not use for JS code (see webMain)
fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            coreModule,
            interopTestPresentationModule
        )
    }
}