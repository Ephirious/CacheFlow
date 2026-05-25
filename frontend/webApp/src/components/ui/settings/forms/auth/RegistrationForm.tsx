import {
    RegistrationAction,
    RegistrationComponent,
    RegistrationIntent,
    RegistrationStep
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../../../../interop/useActions.ts";

interface RegistrationFormProps {
    component: RegistrationComponent
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary w-full";

const RegistrationForm = ({component}: RegistrationFormProps) => {

    const state = useValue(component.state)

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
                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new RegistrationIntent.ChangeName(e.target.value))}
                            placeholder="Ваше имя"
                            type="text"
                            value={state.nameInput}
                        />
                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new RegistrationIntent.ChangeEmail(e.target.value))}
                            placeholder="Ваша почта"
                            type="email"
                            value={state.emailInput}
                        />
                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new RegistrationIntent.ChangePassword(e.target.value))}
                            placeholder="Придумайте пароль"
                            type="password"
                            value={state.passwordInput}
                        />

                        <button 
                            className="mt-2 w-full rounded-xl bg-brand-primary py-3 text-base font-semibold text-brand-on-primary disabled:opacity-50"
                            disabled={state.isLoading}
                            onClick={() => component.intent(RegistrationIntent.SubmitRegistrationClicked)}
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

                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new RegistrationIntent.ChangeCode(e.target.value))}
                            placeholder="Код из письма"
                            type="text"
                            value={state.codeInput}
                        />

                        <button 
                            className="text-sm font-medium text-brand-primary text-center"
                            onClick={() => component.intent(RegistrationIntent.ResendCodeClicked)}
                        >
                            Не пришёл код? Отправить снова
                        </button>

                        <button 
                            className="mt-2 w-full rounded-xl bg-brand-primary py-3 text-base font-semibold text-brand-on-primary disabled:opacity-50"
                            disabled={state.isLoading}
                            onClick={() => component.intent(RegistrationIntent.SubmitCodeClicked)}
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
