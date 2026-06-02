import {
    RegistrationAction,
    RegistrationComponent,
    RegistrationIntent,
    RegistrationStep,
    localz
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../../../../interop/useActions.ts";
import { useState } from "react";

interface RegistrationFormProps {
    component: RegistrationComponent
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary w-full";

const RegistrationForm = ({component}: RegistrationFormProps) => {

    const state = useValue(component.state)
    const [touched, setTouched] = useState({
        name: false,
        email: false,
        password: false,
        code: false
    });

    const hasDetailsErrors = state.validation.nameInput !== null || state.validation.emailInput !== null || state.validation.passwordInput !== null;
    const hasCodeErrors = state.validation.codeInput !== null;

    useActions(component, (action) => {
        when(action)
            .on(RegistrationAction.Error, (error) => {
                alert(error.msg)
            })
            .is(RegistrationAction.CodeSent, () => {
                alert("Код отправлен")
            })
            .run()
    });

    return (
        <div className="flex flex-col gap-4 w-full">
            {when(state.step)
                .is(RegistrationStep.InputDetails, () => (
                    <>
                        <div className="flex flex-col gap-1 w-full">
                            <input
                                className={inputClass}
                                onChange={(e) => component.intent(new RegistrationIntent.ChangeName(e.target.value))}
                                onBlur={() => setTouched(prev => ({...prev, name: true}))}
                                placeholder="Ваше имя"
                                type="text"
                                value={state.nameInput}
                            />
                            {touched.name && state.validation.nameInput && (
                                <span className="text-xs text-state-danger px-1">
                                    {localz.get().byValidation(state.validation.nameInput)}
                                </span>
                            )}
                        </div>
                        <div className="flex flex-col gap-1 w-full">
                            <input
                                className={inputClass}
                                onChange={(e) => component.intent(new RegistrationIntent.ChangeEmail(e.target.value))}
                                onBlur={() => setTouched(prev => ({...prev, email: true}))}
                                placeholder="Ваша почта"
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
                                onChange={(e) => component.intent(new RegistrationIntent.ChangePassword(e.target.value))}
                                onBlur={() => setTouched(prev => ({...prev, password: true}))}
                                placeholder="Придумайте пароль"
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
                                hasDetailsErrors 
                                    ? "bg-state-disabled-bg text-state-disabled-text cursor-not-allowed" 
                                    : "bg-brand-primary text-brand-on-primary hover:opacity-90 active:scale-[0.98]"
                            } disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100`}
                            disabled={state.isLoading || hasDetailsErrors}
                            onClick={() => {
                                if (hasDetailsErrors) {
                                    setTouched(prev => ({...prev, name: true, email: true, password: true}));
                                    return;
                                }
                                component.intent(RegistrationIntent.SubmitRegistrationClicked)
                            }}
                        >
                            {state.isLoading ? "Загрузка..." : "Далее"}
                        </button>
                    </>
                ))
                .is(RegistrationStep.EnterCode, () => (
                    <>
                        <p className="text-sm text-text-secondary text-center px-4">
                            На вашу почту <b>{state.emailInput}</b> придёт код для подтверждения. Введите его ниже.
                        </p>

                        <div className="flex flex-col gap-1 w-full">
                            <input
                                className={inputClass}
                                onChange={(e) => component.intent(new RegistrationIntent.ChangeCode(e.target.value))}
                                onBlur={() => setTouched(prev => ({...prev, code: true}))}
                                placeholder="Код из письма"
                                type="text"
                                value={state.codeInput}
                            />
                            {touched.code && state.validation.codeInput && (
                                <span className="text-xs text-state-danger px-1">
                                    {localz.get().byValidation(state.validation.codeInput)}
                                </span>
                            )}
                        </div>

                        <button 
                            className="text-sm font-medium text-brand-primary text-center cursor-pointer transition-all hover:opacity-80 active:scale-95"
                            onClick={() => component.intent(RegistrationIntent.ResendCodeClicked)}
                        >
                            Не пришёл код? Отправить снова
                        </button>

                        <button 
                            className={`mt-2 w-full rounded-xl py-3 text-base font-semibold transition-all cursor-pointer ${
                                hasCodeErrors 
                                    ? "bg-state-disabled-bg text-state-disabled-text cursor-not-allowed" 
                                    : "bg-brand-primary text-brand-on-primary hover:opacity-90 active:scale-[0.98]"
                            } disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100`}
                            disabled={state.isLoading || hasCodeErrors}
                            onClick={() => {
                                if (hasCodeErrors) {
                                    setTouched(prev => ({...prev, code: true}));
                                    return;
                                }
                                component.intent(RegistrationIntent.SubmitCodeClicked)
                            }}
                        >
                            {state.isLoading ? "Проверка..." : "Подтвердить"}
                        </button>
                    </>
                ))
                .run()
            }
        </div>
    );
};

export default RegistrationForm;
