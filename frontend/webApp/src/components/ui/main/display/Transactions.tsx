import { LiaFilterSolid } from "react-icons/lia";
import {motion} from "framer-motion";
import { TransactionCard } from "./index";
import { prettyDate, Transaction } from "k2ts";
import { useMemo, useState } from "react";


const Transactions = ({
    transactions,
    onEditClick
}: {
    transactions: readonly Transaction[];
    onEditClick: (transactionId: string) => void;
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
        <div className="flex flex-col w-full p-6 pt-0 gap-4">
            <div className="flex w-full items-center justify-between">
                <span className="text-xl font-bold">Транзакции</span>
                <motion.div
                    whileHover={{backgroundColor: "bg-red"}}
                    className="
                    flex px-4 py-2 items-center rounded-2xl border-border-default border gap-2 cursor-pointer bg-surface-base-soft
                    "
                >
                    <LiaFilterSolid className="w-4 h-4 text-base font-medium"/>
                    Фильтры
                </motion.div>
            </div>
            <div className="flex flex-col gap-3 pb-4">
                {sections.map((section, sectionIndex) => (
                    <div key={`${section.dateLabel}-${sectionIndex}`} className="flex flex-col gap-2">
                        <div className="sticky top-[-10px] z-20 -mx-1 rounded-md bg-surface-base px-1 py-1">
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
            </div>
        </div>
    )
}

export default Transactions;
