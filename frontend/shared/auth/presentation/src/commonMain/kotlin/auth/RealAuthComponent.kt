package auth

import auth.AuthChild.LoginChild
import auth.AuthChild.RegistrationChild
import auth.login.RealLoginComponent
import auth.login.mvi.LoginContainer
import auth.registration.RealRegistrationComponent
import auth.registration.mvi.RegistrationContainer
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import utils.interop.JsChildPages
import utils.interop.JsValue
import utils.interop.asJsPages
import utils.path
import utils.presentation.CustomPagesWebNavigation

class RealAuthComponent(
    componentCtx: ComponentContext,
    override val backToSettings: () -> Unit,
) : AuthComponent, ComponentContext by componentCtx, KoinComponent {

    override fun onOutput(output: AuthOutput) {
        when (output) {
            AuthOutput.NavigateToRegistration -> nav.select(AuthConfig.Registration.index)
            AuthOutput.NavigateToLogin -> nav.select(AuthConfig.Login.index)
        }
    }

    override val nav = PagesNavigation<AuthConfig>()
    private val _pages = childPages(
        source = nav,
        serializer = AuthConfig.serializer(),
        initialPages = {
            Pages(
                items = AuthConfig.list(),
                selectedIndex = AuthConfig.Registration.index,
            )
        },
        childFactory = ::pagesChild,
        handleBackButton = false
    )


    override val pages: Value<ChildPages<AuthConfig, AuthChild>>
        get() = _pages

    override val jsPages: JsValue<JsChildPages<AuthChild>> by lazy { _pages.asJsPages() }


    override val webNavigation: WebNavigation<*> =
        CustomPagesWebNavigation(
            navigator = nav,
            pages = _pages,
            serializer = AuthConfig.serializer(),
            pathMapper = { config -> config.path() },
            childSelector = { _ ->
                null
            }
        )


    fun pagesChild(config: AuthConfig, childCtx: ComponentContext): AuthChild {
        return when (config) {
            AuthConfig.Login -> LoginChild(
                component = RealLoginComponent(
                    componentCtx = childCtx,
                    container = {
                        LoginContainer(
                            loginUseCase = get(),
                            onNavigateBack = backToSettings,
                            onLoginFinished = backToSettings,
                        )
                    }
                )
            )

            AuthConfig.Registration -> RegistrationChild(
                component = RealRegistrationComponent(
                    childCtx,
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
            )
        }
    }
}