package settings.pages.categories

import kotlinx.serialization.Serializable
import settings.pages.SettingsPageComponent
import settings.pages.categories.page.CategoriesComponent
import utils.presentation.DefaultPages
import kotlin.js.JsExport


@JsExport
interface CategoriesPagesComponent : SettingsPageComponent, DefaultPages<CategoriesPagesConfig, CategoriesComponent> {
    fun onOutput(output: CategoriesPagesOutput)
}

@Serializable
sealed class CategoriesPagesConfig(val index: Int) {
    @Serializable
    data object Income : CategoriesPagesConfig(1)

    @Serializable
    data object Outcome : CategoriesPagesConfig(0)

    companion object {
        val list: List<CategoriesPagesConfig> = listOf(Income, Outcome).sortedBy { it.index }
    }
}

@JsExport
sealed class CategoriesPagesOutput {
    data object NavigateToIncome : CategoriesPagesOutput()
    data object NavigateToOutcome : CategoriesPagesOutput()
}