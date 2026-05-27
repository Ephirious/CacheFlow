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

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary w-full";

const LoginForm = ({component}: LoginFormProps) => {

    const state = useValue(component.state)

    useActions(component, (action) => {
        when(action)
            .on(LoginAction.Error, (error) => {
                alert(error.msg)
            })
            .run()
    });

    return (
        <div className="flex flex-col gap-4 w-full">
            <input
                className={inputClass}
                onChange={(e) => component.intent(new LoginIntent.ChangeEmail(e.target.value))}
                placeholder="Почта"
                type="email"
                value={state.emailInput}
            />

            <input
                className={inputClass}
                onChange={(e) => component.intent(new LoginIntent.ChangePassword(e.target.value))}
                placeholder="Пароль"
                type="password"
                value={state.passwordInput}
            />

            <button 
                className="mt-2 w-full rounded-xl bg-brand-primary py-3 text-base font-semibold text-brand-on-primary disabled:opacity-50"
                disabled={state.isLoading}
                onClick={() => component.intent(LoginIntent.SubmitClicked)}
            >
                {state.isLoading ? "Загрузка..." : "Войти"}
            </button>
        </div>
    );
};

export default LoginForm;
