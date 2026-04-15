import {FaArrowTrendUp, FaArrowTrendDown} from "react-icons/fa6";
import {LuArrowRightLeft} from "react-icons/lu";
import {Transaction, TransactionType} from "k2ts";
import {FaArrowRight} from "react-icons/fa";
import {when} from "interop";


const TransactionCard = ({transaction}: { transaction: Transaction }) => {
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
        <div className="flex items-center justify-between rounded-xl bg-surface-base-soft px-3 py-2.5 gap-3">
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
        </div>
    )
}

export default TransactionCard;
