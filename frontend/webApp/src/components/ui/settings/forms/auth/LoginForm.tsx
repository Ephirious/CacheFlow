import {
    LoginAction,
    LoginComponent,
    LoginIntent,
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../../../../interop/useActions.ts";

interface LoginFormProps {
    component: LoginComponent
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary";

const LoginForm = ({component}: LoginFormProps) => {

    const state = useValue(component.state)


    // TODO: переделать на нормальные снейкбары
    useActions(component, (action) => {
        when(action)
            .on(LoginAction.Error, (error) => {
                alert(error.msg)
            })
            .run()
    });

    return <>
        <>ВХОД</>
        <button style={{margin: 5}}
                onClick={() => component.intent(LoginIntent.BackClicked)}>
            Назад
        </button>


        <input
            className={inputClass}
            onChange={(e) => component.intent(new LoginIntent.ChangeEmail(e.target.value))}
            placeholder="Почта"
            type="text"
            value={state.emailInput}
        />

        <input
            className={inputClass}
            onChange={(e) => component.intent(new LoginIntent.ChangePassword(e.target.value))}
            placeholder="Пароль"
            type="password"
            value={state.passwordInput}
        />

        <button style={{margin: 10}}
                onClick={() => component.intent(LoginIntent.SubmitClicked)}>
            Войти
        </button>

        {
            state.isLoading && <>Loading</>
        }
    </>

};

export default LoginForm;
