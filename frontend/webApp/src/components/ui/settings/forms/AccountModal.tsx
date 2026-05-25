import {useEffect} from "react";
import {AccountItem} from "../types.ts";
import {accountColorOptions} from "../data.tsx";
import SettingsModalShell from "./SettingsModalShell.tsx";
import {
    CreateAccountComponent,
    EditAccountComponent,
    CreateAccountIntent,
    CreateAccountState,
    EditAccountState,
    ManageAccountBaseIntent,
    EditAccountIntent
} from "k2ts"
import {useValue} from "interop";

interface AccountModalProps {
    component: CreateAccountComponent | EditAccountComponent
    open: boolean;
    mode: "add" | "edit";
    account?: AccountItem;
    onClose: () => void;
}

const inputClass = "h-11 rounded-xl border border-border-default bg-surface-muted px-3 text-base outline-none placeholder:text-text-muted text-text-primary";

const AccountModal = ({component, open, mode, account, onClose}: AccountModalProps) => {
    useEffect(() => {
    }, [account, open]);

    const isAddMode = mode === "add";

    const state = useValue(component.state)

    if (state instanceof CreateAccountState.OK || state instanceof EditAccountState.OK) {
        const canSubmit = state.getForm().title.trim().length > 0;
        return (
            <SettingsModalShell onClose={onClose} open={open}
                                title={isAddMode ? "Добавить счёт" : "Редактировать счёт"}>
                <form
                    className="flex flex-col gap-3"
                    onSubmit={(e) => {
                        e.preventDefault();
                        if (!canSubmit) return;
                        component.intent(state instanceof CreateAccountState.OK ? CreateAccountIntent.ClickedCreate : EditAccountIntent.ClickedEdit);
                    }}
                >
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-text-label">Название</label>
                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new ManageAccountBaseIntent.ChangedTitle(e.target.value))}
                            placeholder="Наличные, Карта..."
                            type="text"
                            value={state.getForm().title}
                        />
                    </div>

                    {isAddMode && component.type === 'create' && (
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-semibold text-text-label">Начальный баланс</label>
                            <input
                                className={inputClass}
                                inputMode="decimal"
                                onChange={(e) => component.intent(new CreateAccountIntent.ChangedBalance(e.target.value))}
                                placeholder="0"
                                type="text"
                                value={(state as CreateAccountState.OK).getForm().initialBalance}
                            />
                        </div>
                    )}

                    <div className="flex flex-col gap-2">
                        <p className="text-sm font-semibold text-text-label">Цвет</p>
                        <div className="grid grid-cols-5 gap-2">
                            {accountColorOptions.map((item) => {
                                const isActive = state.getForm().color.normalizedHex === item;
                                return (
                                    <button
                                        key={item}
                                        style={{backgroundColor: item}}
                                        className={`h-14 w-full rounded-xl ${isActive ? "ring-2 ring-text-primary ring-offset-2 ring-offset-surface-sheet" : ""}`}
                                        onClick={() => component.intent(new ManageAccountBaseIntent.ChangedColor(item))}
                                        type="button"
                                    />
                                );
                            })}
                        </div>
                    </div>

                    <button
                        className={`mt-1 h-11 rounded-xl text-base font-semibold text-brand-on-primary ${canSubmit ? "bg-brand-primary" : "bg-state-disabled-bg text-state-disabled-text"}`}
                        disabled={!canSubmit}
                        type="submit"
                    >
                        {isAddMode ? "Добавить" : "Сохранить"}
                    </button>
                    {!isAddMode && (
                        <button
                            className="h-11 rounded-xl border border-state-danger text-base font-semibold text-state-danger"
                            onClick={() => {
                                if (window.confirm("Удалить счёт?")) {
                                    component.intent(EditAccountIntent.ClickedDelete);
                                }
                            }}
                            type="button"
                        >
                            Удалить
                        </button>
                    )}
                </form>
            </SettingsModalShell>
        );
    }
};

export default AccountModal;
