package root

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import kotlinx.serialization.KSerializer
class CustomPagesWebNavigation<C : Any, T : Any>(
    private val navigator: PagesNavigation<C>,
    pages: Value<ChildPages<C, T>>,
    serializer: KSerializer<C>,
    private val pathMapper: (C) -> String? = { null },
    private val parametersMapper: (C) -> Map<String, String>? = { null },
    private val getHistory: () -> List<Int>,
    private val childSelector: (Child.Created<C, T>) -> WebNavigationOwner? = { null },
    private val onBeforeNavigate: () -> Boolean = { true },
) : WebNavigation<Pages<C>> {

    override val serializer: KSerializer<Pages<C>> = Pages.serializer(serializer)

    override val history: Value<List<WebNavigation.HistoryItem<Pages<C>>>> =
        pages.map { currentState ->
            val indices = getHistory()

            indices.map { index ->
                val child = currentState.items[index]
                val config = child.configuration

                WebNavigation.HistoryItem(
                    path = pathMapper(config) ?: "",
                    parameters = parametersMapper(config) ?: emptyMap(),
                    key = Pages(
                        items = currentState.items.map { it.configuration },
                        selectedIndex = index,
                    ),
                    child = (child as? Child.Created)?.let(childSelector),
                )
            }
        }

    override fun navigate(history: List<Pages<C>>) {
        val lastPagesState = history.lastOrNull()
        if (lastPagesState != null) {
            navigator.select(index = lastPagesState.selectedIndex)
        }
    }

    override fun onBeforeNavigate(): Boolean = onBeforeNavigate.invoke()
}