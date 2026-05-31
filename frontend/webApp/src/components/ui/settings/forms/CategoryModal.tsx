import {useEffect, useState} from "react";
import {CategoryItem} from "../types.ts";
import {SegmentedTabs} from "../primitives";
import {categoryTypeTabs} from "../data.tsx";
import SettingsModalShell from "./SettingsModalShell.tsx";
import {
    CategoryType,
    CreateCategoryComponent, EditCategoryComponent,
    CreateCategoryIntent,
    CreateCategoryState,
    ManageCategoryBaseIntent,
    EditCategoryState,
    EditCategoryIntent,
    localz
} from "k2ts";
import {useValue} from "interop";

interface CategoryModalProps {
    open: boolean;
    component: CreateCategoryComponent | EditCategoryComponent
    mode: "add" | "edit";
    category?: CategoryItem;
    onClose: () => void;
}

const inputClass = "flex py-3 px-4 rounded-xl border border-border-default bg-surface-muted text-base outline-none placeholder:text-text-muted text-text-primary";

const basicEmojis = ['🛒', '🚗', '🏠', '🍔', '💊', '👕', '🎁', '✈️', '🐶'];

const CategoryModal = ({open, component, mode, category, onClose}: CategoryModalProps) => {

    const state = useValue(component.state)
    const [touched, setTouched] = useState({ title: false, emoji: false });

    useEffect(() => {
        if (!open) return;
        setTouched({ title: false, emoji: false });
    }, [category, open]);

    const isAddMode = mode === "add";
    if (state instanceof CreateCategoryState.OK || state instanceof EditCategoryState.OK) {
        const validation = state.getForm().validation;
        const hasError = Boolean(validation.title || validation.emoji);

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
                        if (hasError) {
                            setTouched({ title: true, emoji: true });
                            return;
                        }
                        component.intent(state instanceof CreateCategoryState.OK ? CreateCategoryIntent.ClickedCreate : EditCategoryIntent.ClickedEdit);
                    }}
                >
                    <div className="flex flex-col gap-1">
                        <input
                            className={inputClass}
                            onChange={(e) => component.intent(new ManageCategoryBaseIntent.ChangedTitle(e.target.value))}
                            onBlur={() => setTouched(prev => ({ ...prev, title: true }))}
                            placeholder="Название категории"
                            type="text"
                            value={state.getForm().title}
                        />
                        {touched.title && validation.title && (
                            <span className="text-xs text-state-danger px-1">
                                {localz.get().byValidation(validation.title!)}
                            </span>
                        )}
                    </div>
                    <div className="flex flex-col gap-2">
                        <p className="text-sm font-semibold text-text-label">Иконка</p>
                        <div className="grid grid-cols-5 gap-2">
                            {basicEmojis.map((emoji) => {
                                const isActive = state.getForm().emoji === emoji;
                                return (
                                    <button
                                        key={emoji}
                                        className={`flex items-center justify-center h-14 w-full rounded-xl bg-surface-muted text-2xl cursor-pointer transition-transform hover:scale-105 active:scale-95 ${isActive ? "ring-2 ring-text-primary ring-offset-2 ring-offset-surface-sheet" : ""}`}
                                        onClick={() => component.intent(new ManageCategoryBaseIntent.ChangedEmoji(emoji))}
                                        type="button"
                                    >
                                        {emoji}
                                    </button>
                                );
                            })}
                            <input
                                className={`flex text-center items-center justify-center h-14 w-full rounded-xl text-2xl outline-none transition-all focus:scale-105 ${!basicEmojis.includes(state.getForm().emoji) && state.getForm().emoji !== "" ? "ring-2 ring-text-primary ring-offset-2 ring-offset-surface-sheet bg-surface-muted" : "bg-surface-base border border-border-default placeholder:text-text-muted"}`}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    const chars = Array.from(val);
                                    const nextEmoji = chars.length > 0 ? chars[chars.length - 1] : "";
                                    component.intent(new ManageCategoryBaseIntent.ChangedEmoji(nextEmoji));
                                }}
                                onBlur={() => setTouched(prev => ({ ...prev, emoji: true }))}
                                placeholder="🙂"
                                title="Ввести свой эмодзи"
                                type="text"
                                value={!basicEmojis.includes(state.getForm().emoji) ? state.getForm().emoji : ""}
                            />
                        </div>
                        {touched.emoji && validation.emoji && (
                            <span className="text-xs text-state-danger px-1">
                                {localz.get().byValidation(validation.emoji!)}
                            </span>
                        )}
                    </div>

                    {isAddMode && component.type === 'create' && (
                        <SegmentedTabs
                            active={(state as CreateCategoryState.OK).getForm().categoryType === CategoryType.income ? "income" : "outcome"}
                            onChange={(newCategory) => component.intent(new CreateCategoryIntent.ChangedCategoryType(newCategory))}
                            options={categoryTypeTabs}/>
                    )}

                    <button
                        className={`h-11 rounded-xl text-base font-semibold text-brand-on-primary transition-all ${!hasError ? "bg-brand-primary cursor-pointer hover:opacity-90 active:scale-[0.98]" : ((touched.title || touched.emoji) ? "bg-state-disabled-bg text-state-disabled-text cursor-not-allowed" : "bg-brand-primary cursor-pointer hover:opacity-90 active:scale-[0.98]")}`}
                        type="submit"
                    >
                        {isAddMode ? "Добавить" : "Сохранить"}
                    </button>
                    {!isAddMode && (
                        <button
                            className="h-11 rounded-xl border border-state-danger text-base font-semibold text-state-danger cursor-pointer transition-colors hover:bg-state-danger/10 active:bg-state-danger/20"
                            onClick={() => {
                                if (window.confirm("Удалить категорию?")) {
                                    component.intent(EditCategoryIntent.ClickedDelete);
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

export default CategoryModal;
