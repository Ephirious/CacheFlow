package core

import core.ktor.ktorModule
import org.koin.dsl.module

val coreModule = module {
    includes(ktorModule)
}