import {useEffect} from "react";
import {CategoryItem} from "../types.ts";
import {SegmentedTabs} from "../primitives";
import {categoryTypeTabs} from "../data.tsx";
import SettingsModalShell from "./SettingsModalShell.tsx";
import {
    CategoryType,
    CreateCategoryComponent,
    CreateCategoryIntent,
    CreateCategoryState,
    ManageCategoryBaseIntent
} from "k2ts";
import {useValue} from "interop";

interface CategoryModalProps {
    open: boolean;
    component: CreateCategoryComponent | CreateCategoryComponent
    mode: "add" | "edit";
    category?: CategoryItem;
    onClose: () => void;
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted";

const CategoryModal = ({open, component, mode, category, onClose}: CategoryModalProps) => {

    const state = useValue(component.state)


    useEffect(() => {
        if (!open) return;
    }, [category, open]);

    const isAddMode = mode === "add";
    if (state instanceof CreateCategoryState.OK) {
        const canSubmit = state.getForm().name.trim().length > 0;

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
                        onChange={(e) => component.intent(new ManageCategoryBaseIntent.ChangedName(e.target.value))}
                        placeholder="Название категории"
                        type="text"
                        value={state.getForm().name}
                    />
                    <input
                        className={inputClass}
                        onChange={(e) => component.intent(new ManageCategoryBaseIntent.ChangedEmoji(e.target.value))}
                        placeholder="Иконка (необязательно)"
                        type="text"
                        value={state.getForm().emoji}
                    />

                    {isAddMode && component.type === 'create' && (
                        <SegmentedTabs
                            active={state.getForm().categoryType === CategoryType.INCOME ? "income" : "outcome"}
                            onChange={(newCategory) => component.intent(new CreateCategoryIntent.ChangedCategoryType(newCategory))}
                            options={categoryTypeTabs}/>
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
    }
};

export default CategoryModal;
