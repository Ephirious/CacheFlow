import { LiaFilterSolid } from "react-icons/lia";
import { LuArrowRightLeft } from "react-icons/lu";
import {motion} from "framer-motion";
import { TransactionCard } from "./index";
import { prettyDate, Transaction } from "k2ts";
import { useMemo, useState } from "react";


const Transactions = ({
    transactions,
    hasActiveFilters,
    onEditClick,
    onLoadMore,
    onFilterClick
}: {
    transactions: readonly Transaction[];
    hasActiveFilters?: boolean;
    onEditClick: (transactionId: string) => void;
    onLoadMore?: () => void;
    onFilterClick?: () => void;
}) => {
    const sections = useMemo(() => {
        return transactions.reduce<Array<{ dateLabel: string; items: Transaction[] }>>((acc, transaction) => {
            const dateLabel = prettyDate(transaction.date);
            const lastSection = acc[acc.length - 1];

            if (lastSection && lastSection.dateLabel === dateLabel) {
                lastSection.items.push(transaction);
            } else {
                acc.push({ dateLabel, items: [transaction] });
            }

            return acc;
        }, []);
    }, [transactions]);

    const [expandedId, setExpandedId] = useState<string | null>(null);

    const onTransactionClick = (transactionId: string) => {
        setExpandedId((prev) => prev === transactionId ? null : transactionId);
    };


    return (
        <div className="flex w-full flex-col gap-4 p-6 pt-0 md:pt-6">
            <div className="flex w-full items-center justify-between">
                <span className="text-xl font-bold text-text-primary">Транзакции</span>
                <motion.div
                    onClick={onFilterClick}
                    className="
                    bg-surface-muted relative
                    flex px-4 py-2 items-center rounded-2xl border-border-default border gap-2 cursor-pointer
                    "
                >
                    <LiaFilterSolid className={hasActiveFilters ? "w-4 h-4 text-base font-medium text-brand-primary" : "w-4 h-4 text-base font-medium text-text-primary"}/>
                    <p className={hasActiveFilters ? "font-medium text-brand-primary" : "text-text-primary"}>Фильтры</p>
                    {hasActiveFilters && (
                        <span className="absolute top-1.5 right-2 w-2 h-2 rounded-full bg-brand-primary" />
                    )}
                </motion.div>
            </div>
            {transactions.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12 text-center">
                    <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-surface-muted">
                        <LuArrowRightLeft className="h-10 w-10 text-text-secondary" />
                    </div>
                    <h3 className="mb-2 text-lg font-semibold text-text-primary">Нет транзакций</h3>
                    <p className="text-sm text-text-secondary">Нажмите + чтобы добавить первую транзакцию</p>
                </div>
            ) : (
                <div className="flex flex-col gap-3 pb-4">
                    {sections.map((section, sectionIndex) => (
                        <div key={`${section.dateLabel}-${sectionIndex}`} className="flex flex-col gap-2">
                            <div className="sticky top-[-10px] z-20 -mx-1 rounded-md px-1 py-1 md:top-0">
                                <span className="text-xs font-semibold uppercase tracking-wide text-text-secondary">
                                    {section.dateLabel}
                                </span>
                            </div>
                            <div className="flex flex-col gap-2">
                                {section.items.map((transaction, transactionIndex) => {
                                    const transactionId = transaction.id;
                                    return (
                                        <TransactionCard
                                            key={transactionId ?? `${section.dateLabel}-${transactionIndex}`}
                                            transaction={transaction}
                                            isExpanded={transactionId ? expandedId === transactionId : false}
                                            onClick={transactionId ? () => onTransactionClick(transactionId) : undefined}
                                            onEditClick={transactionId ? () => onEditClick(transactionId) : undefined}
                                        />
                                    );
                                })}
                            </div>
                        </div>
                    ))}
                    {transactions.length >= 25 && onLoadMore && (
                        <button
                            onClick={onLoadMore}
                            className="mt-2 w-full rounded-xl border border-border-default bg-surface-muted py-3 text-sm font-medium text-text-primary cursor-pointer transition-all hover:bg-surface-hover active:scale-[0.98]"
                            type="button"
                        >
                            Показать ещё
                        </button>
                    )}
                </div>
            )}
        </div>
    )
}

export default Transactions;
