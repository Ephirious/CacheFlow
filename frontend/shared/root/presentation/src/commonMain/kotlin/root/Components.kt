package root

import com.arkivanov.decompose.ComponentContext
import interopSampleFlow.RealInteropSampleFlowComponent
import main.RealMainComponent
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
                container = get()
            )
        }
    val main =
        ctx.persistent { ctx ->
            RealMainComponent(
                componentCtx = ctx,
                container = get()
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