import { SegmentedControl, DatePicker, TextArea } from "../primitives";
import { CategorySelector, AccountSelector, TransferCard } from "../controls";
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
import {useState} from "react";

interface CreateTransactionProps {
    component: ManageTransactionComponent;
    state: ManageTransactionState.OK;
    close: () => void;
}

const CreateTransactionContent = ({component, state}: CreateTransactionProps) => {
    const [touched, setTouched] = useState({
        value: false,
        category: false,
        account: false,
        transferFrom: false,
        transferTo: false
    });

    const hasValidationError = Boolean(state.form.validation.value) || when(state.form.transactionType)
        .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) =>
            Boolean(type.validation.categoryId || type.validation.accountId)
        )
        .on([ManageTransactionType.Transfer], (type) =>
            Boolean(type.validation.fromId || type.validation.toId)
        )
        .otherwise(() => false);

    const valueError = touched.value ? state.form.validation.value : null;
    const categoryError = touched.category
        ? when(state.form.transactionType)
            .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) => type.validation.categoryId)
            .otherwise(() => null)
        : null;
    const accountError = touched.account
        ? when(state.form.transactionType)
            .on([ManageTransactionType.Outcome, ManageTransactionType.Income], (type) => type.validation.accountId)
            .otherwise(() => null)
        : null;
    const transferFromError = touched.transferFrom
        ? when(state.form.transactionType)
            .on([ManageTransactionType.Transfer], (type) => type.validation.fromId)
            .otherwise(() => null)
        : null;
    const transferToError = touched.transferTo
        ? when(state.form.transactionType)
            .on([ManageTransactionType.Transfer], (type) => type.validation.toId)
            .otherwise(() => null)
        : null;

    const revealValidationOnSaveAttempt = () => {
        const isTransfer = state.form.transactionType.type === "Transfer";
        setTouched((prev) => ({
            ...prev,
            value: true,
            category: isTransfer ? prev.category : true,
            account: isTransfer ? prev.account : true,
            transferFrom: isTransfer ? true : prev.transferFrom,
            transferTo: isTransfer ? true : prev.transferTo
        }));
    };

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
                        onBlur={() => setTouched((prev) => ({...prev, value: true}))}
                        type="text"
                        placeholder="0"
                        className="w-full px-4 py-3 bg-surface-base border border-border-default rounded-xl"
                    />
                    {
                        valueError && localz.get().byValidation(valueError)
                    }
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Тип</span>
                    <SegmentedControl value={state.form.transactionType.type}
                                      onChange={(type) => {
                                          setTouched((prev) => ({
                                              ...prev,
                                              category: false,
                                              account: false,
                                              transferFrom: false,
                                              transferTo: false
                                          }));
                                          component.intent(new ManageTransactionBaseIntent.ChangedType(type));
                                      }}/>
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
                        onSelect={(id: string) => {
                            setTouched((prev) => ({...prev, category: true}));
                            component.intent(new ManageTransactionBaseIntent.ChangedCategory(id))
                        }}
                        onAdd={() => console.log("Add category")}
                    />
                    {
                        categoryError && localz.get().byValidation(categoryError)
                    }
                </div>
                <div className={`flex w-full flex-col gap-2 ${state.form.transactionType.type == 'Transfer' ? "" : "hidden"}`}>
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
                        onSelectFrom={(id) => {
                            setTouched((prev) => ({...prev, transferFrom: true}));
                            component.intent(new ManageTransactionBaseIntent.ChangedAccount(id))
                        }}
                        onSelectTo={(id) => {
                            setTouched((prev) => ({...prev, transferTo: true}));
                            component.intent(new ManageTransactionBaseIntent.ChangedTransferToAccount(id))
                        }}
                    />
                    {transferFromError && localz.get().byValidation(transferFromError)}
                    {transferToError && localz.get().byValidation(transferToError)}
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
                        onSelect={(id: string) => {
                            setTouched((prev) => ({...prev, account: true}));
                            component.intent(new ManageTransactionBaseIntent.ChangedAccount(id))
                        }}
                    />
                    {accountError && localz.get().byValidation(accountError)}
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
                        if (hasValidationError) {
                            revealValidationOnSaveAttempt();
                            return;
                        }
                        component.intent(ManageTransactionIntent.ClickedSave);
                    }}
                    className={`
                    py-4 text-base font-bold rounded-2xl
                    ${hasValidationError ? "bg-state-disabled-bg text-state-disabled-text cursor-not-allowed" : "bg-brand-primary text-brand-on-primary"}
                    `}>
                    Сохранить
                </button>
            </div>
        </div>
    )
}
export default CreateTransactionContent;
