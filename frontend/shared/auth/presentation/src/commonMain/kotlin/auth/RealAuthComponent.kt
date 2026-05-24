package auth

import auth.registration.RealRegistrationComponent
import auth.registration.RegistrationComponent
import auth.registration.mvi.RegistrationContainer
import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RealAuthComponent(
    componentCtx: ComponentContext,
    override val backToSettings: () -> Unit,
) : AuthComponent, ComponentContext by componentCtx, KoinComponent {

    override fun onOutput(output: AuthOutput) {
        TODO("Not yet implemented")
    }

    override val registrationComponent: RegistrationComponent =
        RealRegistrationComponent(
            componentCtx,
            container = {
                RegistrationContainer(
                    registerUseCase = get(),
                    resendCodeUseCase = get(),
                    verifyRegistrationUseCase = get(),
                    onNavigateBack = backToSettings,
                    onRegistrationFinished = backToSettings
                )
            }
        )
}