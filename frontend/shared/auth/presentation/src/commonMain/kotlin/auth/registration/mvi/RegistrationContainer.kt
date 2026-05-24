package auth.registration.mvi

import auth.usecases.RegisterUseCase
import auth.usecases.ResendVerificationCodeUseCase
import auth.usecases.VerifyRegistrationUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<RegistrationState, RegistrationIntent, RegistrationAction>

class RegistrationContainer(
    private val registerUseCase: RegisterUseCase,
    private val resendCodeUseCase: ResendVerificationCodeUseCase,
    private val verifyRegistrationUseCase: VerifyRegistrationUseCase,

    private val onNavigateBack: () -> Unit,
    private val onRegistrationFinished: () -> Unit
) : Container<RegistrationState, RegistrationIntent, RegistrationAction> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<RegistrationState, RegistrationIntent, RegistrationAction> =
        store(
            initial = RegistrationState.getInitial()
        ) {
            fastConfig(
                name = "Registration", resetOnStop = false,
                doOnRecover = { state, e ->
                    action(RegistrationAction.Error(e.message.orUnknown))
                    state.copy(isLoading = false)
                }
            )

            customReduce { intent ->
                when (intent) {
                    RegistrationIntent.BackClicked -> {
                        withState {
                            if (step == RegistrationStep.EnterCode) {
                                updateState { copy(step = RegistrationStep.InputDetails, codeInput = "") }
                            } else {
                                onNavigateBack()
                            }
                        }
                    }

                    RegistrationIntent.ResendCodeClicked -> resendCode()
                    RegistrationIntent.SubmitCodeClicked -> submitCode()
                    RegistrationIntent.SubmitRegistrationClicked -> submitRegistration()


                    is RegistrationIntent.ChangeCode -> {
                        updateState { copy(codeInput = intent.code) }
                    }

                    is RegistrationIntent.ChangeEmail -> {
                        updateState { copy(emailInput = intent.email) }
                    }

                    is RegistrationIntent.ChangeName -> {
                        updateState { copy(nameInput = intent.name) }
                    }

                    is RegistrationIntent.ChangePassword -> {
                        updateState { copy(passwordInput = intent.password) }
                    }
                }
            }
        }


    private suspend fun Ctx.submitRegistration() {
        withState {
            updateState { copy(isLoading = true) }

            val newUserId = registerUseCase(emailInput, passwordInput, nameInput)


            updateState {
                copy(
                    isLoading = false,
                    step = RegistrationStep.EnterCode,
                    savedUserId = newUserId
                )
            }
        }
    }

    private suspend fun Ctx.submitCode() {
        withState {
            val userId = savedUserId ?: return@withState

            updateState { copy(isLoading = true) }

            // если у него не получится распарсить токены – упадёт с ошибкой - упадёт в doOnRecover
            verifyRegistrationUseCase(
                userId = userId,
                code = codeInput
            )

            onRegistrationFinished()
        }
    }

    private suspend fun Ctx.resendCode() {
        withState {
            val userId = this.savedUserId ?: return@withState
            resendCodeUseCase(userId)

            action(RegistrationAction.CodeSent)
        }
    }
}