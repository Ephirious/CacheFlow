package core

import core.ktor.ktorModule
import core.settings.settingsModule
import org.koin.dsl.module

val coreModule = module {
    includes(ktorModule, settingsModule)
}