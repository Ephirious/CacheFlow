package auth.login.mvi

import auth.usecases.LoginUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<LoginState, LoginIntent, LoginAction>

class LoginContainer(
    private val loginUseCase: LoginUseCase,

    private val onNavigateBack: () -> Unit,
    private val onLoginFinished: () -> Unit
) : Container<LoginState, LoginIntent, LoginAction> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<LoginState, LoginIntent, LoginAction> =
        store(
            initial = LoginState.getInitial()
        ) {
            fastConfig(
                name = "Login", resetOnStop = false,
                doOnRecover = { state, e ->
                    action(LoginAction.Error(e.message.orUnknown))
                    state.copy(isLoading = false)
                }
            )

            customReduce { intent ->
                when (intent) {
                    LoginIntent.BackClicked -> {
                        onNavigateBack()
                    }

                    is LoginIntent.ChangeEmail -> {
                        updateState { copy(emailInput = intent.email) }
                    }

                    is LoginIntent.ChangePassword -> {
                        updateState { copy(passwordInput = intent.password) }
                    }

                    LoginIntent.SubmitClicked -> submitLogin()
                }
            }
        }


    private suspend fun Ctx.submitLogin() {
        withState {
            updateState { copy(isLoading = true) }

            loginUseCase(emailInput, passwordInput)

            onLoginFinished()
        }
    }
}