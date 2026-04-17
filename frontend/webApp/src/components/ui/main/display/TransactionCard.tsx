import {FaArrowTrendUp, FaArrowTrendDown} from "react-icons/fa6";
import {LuArrowRightLeft, LuPencil} from "react-icons/lu";
import {Transaction, TransactionType} from "k2ts";
import {FaArrowRight} from "react-icons/fa";
import {when} from "interop";


const TransactionCard = ({
    transaction,
    isExpanded = false,
    onClick,
    onEditClick
}: {
    transaction: Transaction;
    isExpanded?: boolean;
    onClick?: () => void;
    onEditClick?: () => void;
}) => {
    const {value, type, account} = transaction;

    const isIncome = type instanceof TransactionType.Income;
    const isTransfer = type instanceof TransactionType.Transfer;

    const config = {
        Icon: isIncome ? FaArrowTrendUp : isTransfer ? LuArrowRightLeft : FaArrowTrendDown,
        IconColor: isIncome ? "fill-state-success" : isTransfer ? "stroke-brand-primary" : "fill-state-danger",
        IconBg: isIncome ? "bg-state-success-soft" : isTransfer ? "bg-state-brand-soft" : "bg-state-danger-soft",
        priceColor: isIncome ? "text-state-success" : isTransfer ? "text-brand-primary" : "text-state-danger",
        prefix: isIncome ? "+" : isTransfer ? "" : "",
    };

    return (
        <div className="rounded-xl bg-surface-base-soft px-3 py-2.5">
            <button
                type="button"
                onClick={onClick}
                className={`flex w-full items-center justify-between gap-3 text-left ${onClick ? "cursor-pointer" : "cursor-default"}`}
            >
                <div className="flex min-w-0 items-center gap-2.5">
                    <div className={`w-9 h-9 ${config.IconBg} p-2.5 rounded-xl`}>
                        <config.Icon className={`${config.IconColor} w-4 h-4`}/>
                    </div>
                    <div className="flex min-w-0 flex-col">
                        <span className="flex items-center gap-1 truncate text-sm font-semibold leading-tight">
                            {
                                when(type)
                                    .on(TransactionType.Transfer, (transfer) =>
                                        <>
                                            <span className="truncate">{transfer.from.title}</span>
                                            <FaArrowRight className="w-2.5 h-2.5 shrink-0"/>
                                            <span className="truncate">{transfer.to.title}</span>
                                        </>
                                    )
                                    .on([TransactionType.Income, TransactionType.Outcome], ({category}) =>
                                        <span className="truncate">{category.name}</span>
                                    ).run()
                            }
                        </span>
                        {!(type instanceof TransactionType.Transfer) && (
                            <span className="truncate text-xs text-text-secondary">{account.title}</span>
                        )}
                    </div>
                </div>
                <span className={`shrink-0 text-base font-bold ${config.priceColor}`}>
                    {config.prefix}{value.prettyString()}
                </span>
            </button>

            <div
                className={`grid overflow-hidden transition-[grid-template-rows,opacity,margin,padding,border-color] duration-200 ease-out ${isExpanded ? "mt-3 grid-rows-[1fr] border-t border-border-default pt-3 opacity-100" : "pointer-events-none mt-0 grid-rows-[0fr] border-t border-transparent pt-0 opacity-0"}`}
            >
                <div className="min-h-0 overflow-hidden">
                    <div className="flex items-start gap-2 rounded-lg bg-surface-base px-3 py-2">
                        <span className="flex-1 text-sm text-text-primary">
                            {transaction.note.trim().length > 0 ? transaction.note : "Без заметки"}
                        </span>
                        {onEditClick && (
                            <button
                                type="button"
                                onClick={onEditClick}
                                className="shrink-0 rounded-md p-1 text-text-secondary transition-colors hover:bg-surface-base-soft hover:text-text-primary"
                                aria-label="Редактировать"
                            >
                                <LuPencil className="h-4 w-4"/>
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TransactionCard;
