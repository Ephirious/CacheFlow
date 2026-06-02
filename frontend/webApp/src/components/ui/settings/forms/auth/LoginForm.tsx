import {
    LoginAction,
    LoginComponent,
    LoginIntent,
    localz
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../../../../interop/useActions.ts";
import { FiAlertCircle } from "react-icons/fi";
import { useState } from "react";

interface LoginFormProps {
    component: LoginComponent
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary w-full";

const LoginForm = ({component}: LoginFormProps) => {

    const state = useValue(component.state)
    const [touched, setTouched] = useState({
        email: false,
        password: false
    });

    const hasErrors = state.validation.emailInput !== null || state.validation.passwordInput !== null;

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

            <div className="flex flex-col gap-1 w-full">
                <input
                    className={inputClass}
                    onChange={(e) => component.intent(new LoginIntent.ChangeEmail(e.target.value))}
                    onBlur={() => setTouched(prev => ({...prev, email: true}))}
                    placeholder="Почта"
                    type="email"
                    value={state.emailInput}
                />
                {touched.email && state.validation.emailInput && (
                    <span className="text-xs text-state-danger px-1">
                        {localz.get().byValidation(state.validation.emailInput)}
                    </span>
                )}
            </div>

            <div className="flex flex-col gap-1 w-full">
                <input
                    className={inputClass}
                    onChange={(e) => component.intent(new LoginIntent.ChangePassword(e.target.value))}
                    onBlur={() => setTouched(prev => ({...prev, password: true}))}
                    placeholder="Пароль"
                    type="password"
                    value={state.passwordInput}
                />
                {touched.password && state.validation.passwordInput && (
                    <span className="text-xs text-state-danger px-1">
                        {localz.get().byValidation(state.validation.passwordInput)}
                    </span>
                )}
            </div>

            <button 
                className={`mt-2 w-full rounded-xl py-3 text-base font-semibold transition-all cursor-pointer ${
                    hasErrors 
                        ? "bg-state-disabled-bg text-state-disabled-text cursor-not-allowed" 
                        : "bg-brand-primary text-brand-on-primary hover:opacity-90 active:scale-[0.98]"
                } disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100`}
                disabled={state.isLoading || hasErrors}
                onClick={() => {
                    if (hasErrors) {
                        setTouched({ email: true, password: true });
                        return;
                    }
                    component.intent(LoginIntent.SubmitClicked)
                }}
            >
                {state.isLoading ? "Загрузка..." : "Войти"}
            </button>
        </div>
    );
};

export default LoginForm;
