import { useState } from "react";
import { DatePicker } from "../primitives";
import { FiltersComponent, FiltersIntent, TransactionTypeEnum, isoString } from "k2ts";
import { useValue } from "interop";
import { IoChevronDown } from "react-icons/io5";

interface FiltersProps {
    component: FiltersComponent;
}

const Filters = ({ component }: FiltersProps) => {

    const state = useValue(component.state);
    const filters = state.currentFilters;
    const categories = state.categories.asJsReadonlyArrayView();
    const accounts = state.accounts.asJsReadonlyArrayView();

    const [isAccountsOpen, setIsAccountsOpen] = useState(false);

    const selectedAccounts = accounts.filter((a: any) => filters.accountIds.asJsReadonlyArrayView().includes(a.id));
    const accountsTitle = selectedAccounts.length > 0 
        ? selectedAccounts.map((a: any) => a.title).join(", ") 
        : "Все счета";

    return (
        <div className="flex w-full flex-col px-6 py-2 gap-6">
            <div className="flex flex-col gap-3">
                <span className="text-sm font-medium text-text-primary">Тип транзакции</span>
                <div className="flex gap-2 w-full">
                    <button
                        onClick={() => component.intent(new FiltersIntent.ToggleTransactionType(TransactionTypeEnum.income))}
                        className={`flex-1 py-3 px-2 rounded-2xl border text-sm font-medium transition-all cursor-pointer ${filters.allowIncome ? 'border-brand-primary ring-1 ring-brand-primary/20 bg-brand-primary/5 text-text-primary' : 'border-border-default bg-surface-base-soft text-text-secondary hover:bg-surface-hover hover:border-border-strong'}`}
                    >
                        Доход
                    </button>
                    <button
                        onClick={() => component.intent(new FiltersIntent.ToggleTransactionType(TransactionTypeEnum.outcome))}
                        className={`flex-1 py-3 px-2 rounded-2xl border text-sm font-medium transition-all cursor-pointer ${filters.allowOutcome ? 'border-brand-primary ring-1 ring-brand-primary/20 bg-brand-primary/5 text-text-primary' : 'border-border-default bg-surface-base-soft text-text-secondary hover:bg-surface-hover hover:border-border-strong'}`}
                    >
                        Расход
                    </button>
                    <button
                        onClick={() => component.intent(new FiltersIntent.ToggleTransactionType(TransactionTypeEnum.transfer))}
                        className={`flex-1 py-3 px-2 rounded-2xl border text-sm font-medium transition-all cursor-pointer ${filters.allowTransfer ? 'border-brand-primary ring-1 ring-brand-primary/20 bg-brand-primary/5 text-text-primary' : 'border-border-default bg-surface-base-soft text-text-secondary hover:bg-surface-hover hover:border-border-strong'}`}
                    >
                        Перевод
                    </button>
                </div>
            </div>

            <div className="flex gap-4 w-full">
                <div className="flex-1 flex flex-col gap-2">
                    <span className="text-sm font-medium text-text-primary">Дата с</span>
                    <DatePicker
                        value={filters.dateFrom ? new Date(isoString(filters.dateFrom)) : null}
                        onChange={(newDate) => {
                            component.intent(new FiltersIntent.UpdateDateFrom(newDate?.toISOString() || null))
                        }}
                    />
                </div>
                <div className="flex-1 flex flex-col gap-2">
                    <span className="text-sm font-medium text-text-primary">Дата до</span>
                    <DatePicker
                        value={filters.dateTo ? new Date(isoString(filters.dateTo)) : null}
                        onChange={(newDate) => {
                            component.intent(new FiltersIntent.UpdateDateTo(newDate?.toISOString() || null))
                        }}
                    />
                </div>
            </div>

            
            <div className='flex flex-col gap-2'>
                <span className='text-sm font-medium text-text-primary'>Поиск по заметке</span>
                <input
                    type='text'
                    value={filters.noteQuery || ''}
                    onChange={(e) => component.intent(new FiltersIntent.UpdateNote(e.target.value))}
                    placeholder='Введите текст...'
                    className='w-full px-4 py-3 bg-surface-muted rounded-2xl border border-border-strong text-text-primary placeholder:text-text-muted outline-none focus:border-brand-primary transition-colors'
                />
            </div>

            <div className="flex flex-col gap-2">
                <span className="text-sm font-medium text-text-primary">Категории</span>
                <div className="flex gap-3 overflow-x-auto pb-2 no-scrollbar px-1 -mx-1">
                    {categories.map((category: any) => {
                        const isSelected = filters.categoryIds.asJsReadonlyArrayView().includes(category.id);
                        return (
                            <button
                                key={category.id}
                                onClick={() => component.intent(new FiltersIntent.ToggleCategory(category.id))}
                                className={`flex items-center gap-2 px-4 py-3 rounded-2xl border cursor-pointer transition-all active:scale-95 ${isSelected ? 'border-brand-primary ring-1 ring-brand-primary/20 bg-brand-primary/5 shadow-sm' : 'border-border-default bg-surface-base-soft hover:bg-surface-hover hover:border-border-strong'}`}
                            >
                                <span className={`flex whitespace-nowrap text-center font-medium ${isSelected ? 'text-text-primary' : 'text-text-label'}`}>
                                    {category.emoji + " "}
                                    {category.name}
                                </span>
                            </button>
                        );
                    })}
                </div>
            </div>

            <div className="flex flex-col gap-2">
                <span className="text-sm font-medium text-text-primary">Счета</span>
                <div className="relative">
                    <button
                        onClick={() => setIsAccountsOpen(!isAccountsOpen)}
                        className="w-full flex items-center justify-between px-4 py-3.5 bg-surface-muted rounded-2xl border border-border-strong cursor-pointer transition-all hover:bg-surface-hover active:scale-[0.98]"
                    >
                        <span className="text-text-primary truncate overflow-hidden pr-2">
                            {accountsTitle}
                        </span>
                        <IoChevronDown
                            className={`w-5 h-5 text-text-muted transition-transform ${isAccountsOpen ? 'rotate-180' : ''}`}
                        />
                    </button>
                    {isAccountsOpen && (
                        <>
                            <div className="fixed inset-0 z-40" onClick={() => setIsAccountsOpen(false)} />
                            <div className="absolute z-50 w-full mt-2 bg-surface-base rounded-2xl border border-border-strong shadow-lg overflow-hidden">
                                {accounts.map((account: any) => {
                                    const isSelected = filters.accountIds.asJsReadonlyArrayView().includes(account.id);
                                    return (
                                        <button
                                            key={account.id}
                                            onClick={() => component.intent(new FiltersIntent.ToggleAccount(account.id))}
                                            className="w-full flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors hover:bg-surface-hover active:bg-surface-hover/80"
                                        >
                                            <div
                                                className="w-3 h-3 rounded-full shrink-0"
                                                style={{ backgroundColor: account.color.normalizedHex }}
                                            />
                                            <div className="flex-1 text-left min-w-0">
                                                <div className="text-text-primary font-medium truncate overflow-hidden">
                                                    {account.title}
                                                </div>
                                                <div className="text-text-secondary text-sm">
                                                    {account.balance.prettyString()}
                                                </div>
                                            </div>
                                            {isSelected && (
                                                <div className="w-5 h-5 rounded-full bg-brand-primary flex items-center justify-center">
                                                    <svg className="w-3 h-3 text-brand-on-primary" fill="currentColor" viewBox="0 0 20 20">
                                                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                                    </svg>
                                                </div>
                                            )}
                                        </button>
                                    );
                                })}
                            </div>
                        </>
                    )}
                </div>
            </div>

            <div className="flex gap-4 w-full mt-4">
                <button
                    onClick={() => component.intent(FiltersIntent.ResetFilters)}
                    className="flex-1 py-4 text-base font-bold rounded-2xl transition-all cursor-pointer bg-surface-muted text-text-primary hover:bg-surface-hover active:scale-[0.98]"
                >
                    Сбросить
                </button>
                <button
                    onClick={() => component.intent(FiltersIntent.ApplyClicked)}
                    className="flex-1 py-4 text-base font-bold rounded-2xl transition-all cursor-pointer bg-brand-primary text-brand-on-primary hover:opacity-90 active:scale-[0.98]"
                >
                    Применить
                </button>
            </div>
        </div>
    );
};

export default Filters;
