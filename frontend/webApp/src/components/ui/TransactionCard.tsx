import {FaArrowTrendUp, FaArrowTrendDown} from "react-icons/fa6";
import {LuArrowRightLeft} from "react-icons/lu";
import {prettyDate, Transaction, TransactionType} from "k2ts";
import {FaArrowRight} from "react-icons/fa";
import {when} from "interop";


const TransactionCard = ({transaction}: { transaction: Transaction }) => {
    const {value, type, date, account} = transaction;

    const isIncome = type instanceof TransactionType.Income;
    const isTransfer = type instanceof TransactionType.Transfer;

    const config = {
        Icon: isIncome ? FaArrowTrendUp : isTransfer ? LuArrowRightLeft : FaArrowTrendDown,
        IconColor: isIncome ? "fill-transaction-up" : isTransfer ? "stroke-brand-indigo" : "fill-transaction-down",
        IconBg: isIncome ? "bg-transaction-up-bg" : isTransfer ? "bg-transaction-transfer-bg" : "bg-transaction-down-bg",
        priceColor: isIncome ? "text-transaction-up" : isTransfer ? "text-brand-indigo" : "text-transaction-down",
        prefix: isIncome ? "+" : isTransfer ? "" : "-",
    };

    return (
        <div className="flex flex-col p-4 bg-white/80 rounded-2xl gap-3">
            <div className="flex justify-between">
                <div className={`w-12 h-12 ${config.IconBg} p-3 rounded-2xl`}>
                    <config.Icon className={`${config.IconColor} w-6 h-6`}/>
                </div>
                <span
                    className={`text-xl font-bold ${config.priceColor}`}>{config.prefix}{value.prettyString()}</span>
            </div>
            <div className="flex flex-col gap-1">
                <span className="flex items-center text-base font-semibold gap-1">
                    {
                        when(type)
                            .on(TransactionType.Transfer, (transfer) =>
                                <>
                                    {transfer.from.title}
                                    <FaArrowRight className="w-3 h-3"/>
                                    {transfer.to.title}
                                </>
                            )
                            .on([TransactionType.Income, TransactionType.Outcome], ({category}) =>
                                category.name
                            ).run()
                    }
                </span>
                <div className="flex gap-2 text-xs text-transaction">
                    <span>{prettyDate(date)}</span>
                    <span>
                        {!(type instanceof TransactionType.Transfer) ? "•" : ""}
                    </span>
                    <span>
                        {!(type instanceof TransactionType.Transfer) ? account.title : ""}
                    </span>
                </div>
            </div>
        </div>
    )
}

export default TransactionCard;