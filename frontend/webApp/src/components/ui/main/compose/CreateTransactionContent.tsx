import { SegmentedControl, DatePicker, TextArea } from "../inputs";
import { CategorySelector, AccountSelector, TransferCard } from "../selectors";
import {
    ManageTransactionState,
    ManageTransactionComponent,
    ManageTransactionBaseIntent,
    ManageTransactionType,
    localz,
    isoString,
    ManageTransactionIntent,
    ManageTransactionKey
} from "k2ts";
import {when} from "interop";

interface CreateTransactionProps {
    component: ManageTransactionComponent;
    state: ManageTransactionState.OK;
    close: () => void;
}

const CreateTransactionContent = ({component, state}: CreateTransactionProps) => {
    return (
        <div className="flex w-full flex-col px-6 py-2 gap-4">
            <span
                className="flex text-2xl font-bold justify-center">{localz.get().by(ManageTransactionKey.CreateTransaction)}</span>
            <div className="flex flex-col w-full gap-3">
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Сумма</span>
                    <input
                        value={`${state.form.value}`}
                        onChange={(e) => {
                            const formatted = e.target.value.replace(/\u00A0/g, "")
                            const x = formatted.length == 0 ? "" : formatted
                            component.intent(new ManageTransactionBaseIntent.ChangedValue(x));
                        }}
                        type="text"
                        placeholder="0"
                        className="w-full px-4 py-3 bg-white border border-sheet-input rounded-xl"
                    />
                    {
                        state.form.validation.value && localz.get().byValidation(state.form.validation.value)
                    }
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Тип</span>
                    <SegmentedControl value={state.form.transactionType.type}
                                      onChange={(type) => component.intent(new ManageTransactionBaseIntent.ChangedType(type))}/>
                </div>
                <div className={`flex w-full flex-col gap-2 ${state.form.transactionType.type == 'Transfer' ? "hidden" : ""}`}>
                    <span className="text-sm font-medium">Категория</span>
                    <CategorySelector
                        categories={state.form.categories.asJsReadonlyArrayView()}
                        selectedId={
                            when(state.form.transactionType)
                                .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) =>
                                    type.categoryId ?? null
                                )
                                .otherwise(() =>
                                    ""
                                )

                        }
                        onSelect={(id: string) => component.intent(new ManageTransactionBaseIntent.ChangedCategory(id))}
                        onAdd={() => console.log("Add category")}
                    />
                    {
                        when(state.form.transactionType)
                            .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) =>
                                type.validation.categoryId && localz.get().byValidation(type.validation.categoryId)
                            )
                            .otherwise(() =>
                                ""
                            )
                    }
                </div>
                <div>
                    <TransferCard
                        accounts={state.form.accounts.asJsReadonlyArrayView()}
                        fromId={
                            when(state.form.transactionType)
                                .on([ManageTransactionType.Transfer], (t) => t.fromId ?? null)
                                .otherwise(() => null)
                        }
                        toId={
                            when(state.form.transactionType)
                                .on([ManageTransactionType.Transfer], (t) => t.toId ?? null)
                                .otherwise(() => null)
                        }
                        onSelectFrom={(id) => component.intent(
                            new ManageTransactionBaseIntent.ChangedAccount(id)
                        )}
                        onSelectTo={(id) => component.intent(
                            new ManageTransactionBaseIntent.ChangedAccount(id)
                        )}
                    />
                </div>
                <div className={`flex w-full flex-col gap-2 ${state.form.transactionType.type == 'Transfer' ? "hidden" : ""}`}>
                    <span className="text-sm font-medium">Счёт</span>
                    <AccountSelector
                        accounts={state.form.accounts.asJsReadonlyArrayView()}
                        selectedId={
                            when(state.form.transactionType)
                                .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) =>
                                    type.accountId ?? null
                                )
                                .otherwise(() =>
                                    ""
                                )
                        }
                        onSelect={(id: string) => component.intent(new ManageTransactionBaseIntent.ChangedAccount(id))}
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Дата</span>
                    <DatePicker
                        value={new Date(isoString(state.form.date))}
                        onChange={(newDate) => {
                            component.intent(new ManageTransactionBaseIntent.ChangedDate(newDate.toISOString()))
                        }}
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Заметка (необязательно)</span>
                    <TextArea
                        value={state.form.note}
                        onChange={(e) => {
                            component.intent(new ManageTransactionBaseIntent.ChangedNote(e))
                        }}
                        placeholder="Добавьте описание..."
                    />
                </div>
                <button
                    onClick={() => {
                        component.intent(ManageTransactionIntent.ClickedSave);
                    }}
                    className="bg-brand-indigo py-4 text-base font-bold text-white rounded-2xl">Сохранить
                </button>
            </div>
        </div>
    )
}
export default CreateTransactionContent;