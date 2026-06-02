package root

import com.arkivanov.decompose.ComponentContext
import main.RealMainComponent
import main.mvi.MainContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import settings.RealSettingsComponent
import settings.mvi.SettingsContainer
import stats.RealStatsComponent
import utils.Url
import utils.presentation.persistent

internal class PersistentRootComponents(ctx: ComponentContext, deepLinkUrl: Url?) : KoinComponent {
    val main =
        ctx.persistent { ctx ->
            RealMainComponent(ctx, container = { get<MainContainer>() })
        }

    val stats =
        ctx.persistent { ctx ->
            RealStatsComponent(
                componentCtx = ctx
            )
        }
    val settings =
        ctx.persistent { ctx ->
            RealSettingsComponent(
                ctx,
                container = { get<SettingsContainer>() },
                deepLinkUrl = deepLinkUrl
            )
        }
}