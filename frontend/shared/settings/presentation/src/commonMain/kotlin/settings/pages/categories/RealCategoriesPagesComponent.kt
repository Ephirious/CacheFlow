package settings.pages.categories

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import dbEnums.CategoryType
import editors.usecases.category.CategoriesLists
import editors.usecases.category.GetCategoriesFlowUseCase
import settings.pages.categories.page.CategoriesComponent
import settings.pages.categories.page.RealCategoriesComponent
import utils.Url
import utils.consumePathSegment
import utils.interop.asJsPages
import utils.path
import utils.pathSegmentOf
import utils.presentation.CustomPagesWebNavigation
import utils.presentation.launchPersistentCoroutine


class RealCategoriesPagesComponent(
    componentCtx: ComponentContext,
    deepLinkUrl: Url?,
    getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    override val onCreateClick: () -> Unit,
    override val onItemClick: (id: String) -> Unit
) : CategoriesPagesComponent, ComponentContext by componentCtx {




    private val _categories = instanceKeeper.getOrCreateSimple(key = "Categories") {
        MutableValue(CategoriesLists(emptyList(), emptyList()))
    }

    override val nav = PagesNavigation<CategoriesPagesConfig>()
    private val _pages = childPages(
        source = nav,
        serializer = CategoriesPagesConfig.serializer(),
        initialPages = { getInitialPages(deepLinkUrl) },
        childFactory = ::child,
        handleBackButton = false
    )


    override val pages: Value<ChildPages<CategoriesPagesConfig, CategoriesComponent>>
        get() = _pages

    override val jsPages by lazy { _pages.asJsPages() }


    override val webNavigation: WebNavigation<*> =
        CustomPagesWebNavigation(
            navigator = nav,
            pages = _pages,
            serializer = CategoriesPagesConfig.serializer(),
            pathMapper = { config -> config.path() }
        )

    init {
        launchPersistentCoroutine(key = "CategoriesSubscription") {
            getCategoriesFlowUseCase().collect { lists ->
                _categories.value = lists
            }
        }
    }


    private fun child(config: CategoriesPagesConfig, childCtx: ComponentContext): CategoriesComponent {
        return when (config) {

            CategoriesPagesConfig.Income -> RealCategoriesComponent(
                childCtx,
                type = CategoryType.INCOME,
                categories = _categories.map { it.income })

            CategoriesPagesConfig.Outcome -> RealCategoriesComponent(
                childCtx,
                type = CategoryType.OUTCOME,
                categories = _categories.map { it.outcome }
            )
        }
    }

    private fun getInitialPages(deepLinkUrl: Url?): Pages<CategoriesPagesConfig> {

        val (segment, _) = deepLinkUrl?.consumePathSegment() ?: (null to null)

        val selectedConfig = when (segment) {
            pathSegmentOf<CategoriesPagesConfig.Income>() -> CategoriesPagesConfig.Income
            pathSegmentOf<CategoriesPagesConfig.Outcome>() -> CategoriesPagesConfig.Outcome
            else -> CategoriesPagesConfig.Outcome
        }

        return Pages(
            items = CategoriesPagesConfig.list,
            selectedIndex = selectedConfig.index,
        )
    }

    override fun onOutput(output: CategoriesPagesOutput) {
        when (output) {
            CategoriesPagesOutput.NavigateToIncome -> nav.select(CategoriesPagesConfig.Income.index)
            CategoriesPagesOutput.NavigateToOutcome -> nav.select(CategoriesPagesConfig.Outcome.index)
        }
    }
}