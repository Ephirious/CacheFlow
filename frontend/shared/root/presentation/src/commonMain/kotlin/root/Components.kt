package root

import com.arkivanov.decompose.ComponentContext
import interopSampleFlow.RealInteropSampleFlowComponent
import interopSampleFlow.mvi.InteropSampleFlowContainer
import main.RealMainComponent
import main.mvi.MainContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import settings.RealSettingsComponent
import stats.RealStatsComponent
import utils.presentation.persistent

internal class PersistentRootComponents(ctx: ComponentContext) : KoinComponent {
    val interop =
        ctx.persistent { ctx ->
            RealInteropSampleFlowComponent(
                componentCtx = ctx,
                container = { get<InteropSampleFlowContainer>() }
            )
        }
    val main =
        ctx.persistent { ctx ->
            RealMainComponent(
                componentCtx = ctx,
                container = { get<MainContainer>() }
            )
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
                componentCtx = ctx
            )
        }
}