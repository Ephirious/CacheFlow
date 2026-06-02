import {
    LoginAction,
    LoginComponent,
    LoginIntent,
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../../../../interop/useActions.ts";
import { FiAlertCircle } from "react-icons/fi";

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

            <div className="flex items-start gap-2 rounded-xl bg-state-warning-bg/10 p-3 text-sm text-text-primary border border-state-warning-bg/20">
                <FiAlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
                <p>
                    После входа в аккаунт все локальные данные текущей сессии будут удалены.
                </p>
            </div>

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
                className="mt-2 w-full rounded-xl bg-brand-primary py-3 text-base font-semibold text-brand-on-primary transition-all cursor-pointer hover:opacity-90 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100"
                disabled={state.isLoading}
                onClick={() => component.intent(LoginIntent.SubmitClicked)}
            >
                {state.isLoading ? "Загрузка..." : "Войти"}
            </button>
        </div>
    );
};

export default LoginForm;
