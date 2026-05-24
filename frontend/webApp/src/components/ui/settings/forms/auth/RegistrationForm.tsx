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

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary";

const RegistrationForm = ({component}: RegistrationFormProps) => {

    const state = useValue(component.state)


    // TODO: переделать на нормальные снейкбары
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

    return <>
        <>РЕГИСТРАЦИЯ</>
        <button style={{margin: 5}}
                onClick={() => component.intent(RegistrationIntent.BackClicked)}>
            Назад
        </button>
        {when(state.step)
            .is(RegistrationStep.InputDetails, () =>
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
                        type="text"
                        value={state.emailInput}
                    />


                    {/*// TODO: мб потом появится поле "Повторите пароль"*/}
                    <input
                        className={inputClass}
                        onChange={(e) => component.intent(new RegistrationIntent.ChangePassword(e.target.value))}
                        placeholder="Придумайте пароль"
                        type="password"
                        value={state.passwordInput}
                    />

                    <button style={{margin: 10}}
                            onClick={() => component.intent(RegistrationIntent.SubmitRegistrationClicked)}>
                        Далее
                    </button>

                    {
                        state.isLoading && <>Loading</>
                    }
                </>
            )
            .is(RegistrationStep.EnterCode, () =>
                <>
                    <>
                        На вашу почту ({state.emailInput}) придёт код для подтверждения почты. Введите его
                    </>

                    <input
                        className={inputClass}
                        onChange={(e) => component.intent(new RegistrationIntent.ChangeCode(e.target.value))}
                        placeholder="Код"
                        type="text"
                        value={state.codeInput}
                    />

                    <button style={{margin: 5}}
                            onClick={() => component.intent(RegistrationIntent.ResendCodeClicked)}>
                        Не пришёл код? Отправить снова
                    </button>

                    <button style={{margin: 10}}
                            onClick={() => component.intent(RegistrationIntent.SubmitCodeClicked)}>
                        Подтвердить
                    </button>

                    {
                        state.isLoading && <>Loading</>
                    }

                </>
            )
            .run()
        }
    </>

};

export default RegistrationForm;
