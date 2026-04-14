import {useEffect, useState} from "react";
import {AccountItem} from "../types.ts";
import {accountColorOptions} from "../data.tsx";
import SettingsModalShell from "./SettingsModalShell.tsx";

interface AccountModalProps {
    open: boolean;
    mode: "add" | "edit";
    account?: AccountItem;
    onClose: () => void;
}

const inputClass = "h-11 rounded-xl border border-border-default bg-surface-muted px-3 text-base outline-none placeholder:text-text-muted";

const AccountModal = ({open, mode, account, onClose}: AccountModalProps) => {
    const [name, setName] = useState("");
    const [balance, setBalance] = useState("0");
    const [color, setColor] = useState(accountColorOptions[0]);

    useEffect(() => {
        if (!open) return;
        setName(account?.title ?? "");
        setBalance("0");
        setColor(account?.color ?? accountColorOptions[0]);
    }, [account, open]);

    const isAddMode = mode === "add";
    const canSubmit = name.trim().length > 0;

    return (
        <SettingsModalShell onClose={onClose} open={open} title={isAddMode ? "Добавить счёт" : "Редактировать счёт"}>
            <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                    e.preventDefault();
                    if (!canSubmit) return;
                    onClose();
                }}
            >
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-semibold text-text-label">Название</label>
                    <input
                        className={inputClass}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Наличные, Карта..."
                        type="text"
                        value={name}
                    />
                </div>

                {isAddMode && (
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-semibold text-text-label">Начальный баланс</label>
                        <input
                            className={inputClass}
                            inputMode="decimal"
                            onChange={(e) => setBalance(e.target.value)}
                            placeholder="0"
                            type="text"
                            value={balance}
                        />
                    </div>
                )}

                <div className="flex flex-col gap-2">
                    <p className="text-sm font-semibold text-text-label">Цвет</p>
                    <div className="grid grid-cols-5 gap-2">
                        {accountColorOptions.map((item) => {
                            const isActive = color === item;

                            return (
                                <button
                                    key={item}
                                    className={`h-14 w-full rounded-xl ${item} ${isActive ? "ring-2 ring-text-primary ring-offset-2 ring-offset-[#E5E5E7]" : ""}`}
                                    onClick={() => setColor(item)}
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
            </form>
        </SettingsModalShell>
    );
};

export default AccountModal;
