import {useEffect, useState} from "react";
import {CategoryItem, CategoryType} from "../types.ts";
import {SegmentedTabs} from "../primitives";
import {categoryTypeTabs} from "../data.tsx";
import SettingsModalShell from "./SettingsModalShell.tsx";

interface CategoryModalProps {
    open: boolean;
    mode: "add" | "edit";
    category?: CategoryItem;
    onClose: () => void;
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted";

const CategoryModal = ({open, mode, category, onClose}: CategoryModalProps) => {
    const [name, setName] = useState("");
    const [icon, setIcon] = useState("");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");

    useEffect(() => {
        if (!open) return;
        setName(category?.title ?? "");
        setIcon("");
        setCategoryType("expense");
    }, [category, open]);

    const isAddMode = mode === "add";
    const canSubmit = name.trim().length > 0;

    return (
        <SettingsModalShell
            onClose={onClose}
            open={open}
            title={isAddMode ? "Добавить категорию" : "Редактировать категорию"}
        >
            <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                    e.preventDefault();
                    if (!canSubmit) return;
                    onClose();
                }}
            >
                <input
                    className={inputClass}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Название категории"
                    type="text"
                    value={name}
                />
                <input
                    className={inputClass}
                    onChange={(e) => setIcon(e.target.value)}
                    placeholder="Иконка (необязательно)"
                    type="text"
                    value={icon}
                />

                {isAddMode && (
                    <SegmentedTabs active={categoryType} onChange={setCategoryType} options={categoryTypeTabs}/>
                )}

                <button
                    className={`h-11 rounded-xl text-base font-semibold text-brand-on-primary ${canSubmit ? "bg-brand-primary" : "bg-state-disabled-bg text-state-disabled-text"}`}
                    disabled={!canSubmit}
                    type="submit"
                >
                    {isAddMode ? "Добавить" : "Сохранить"}
                </button>
            </form>
        </SettingsModalShell>
    );
};

export default CategoryModal;
