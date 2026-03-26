import { LiaFilterSolid } from "react-icons/lia";
import {motion} from "framer-motion";
import TransactionCard from "./TransactionCard.tsx";
import {Transaction} from "../../types/types.ts"


const Transactions = ({ transactions }: {transactions: Transaction[]}) => {


    return (
        <div className="flex flex-col w-full p-6 pt-0 gap-4">
            <div className="flex w-full items-center justify-between">
                <span className="text-xl font-bold">Транзакции</span>
                <motion.div
                    whileHover={{backgroundColor: "bg-red"}}
                    className="
                    flex px-4 py-2 items-center rounded-2xl border-filter-button border gap-2 cursor-pointer bg-on-brand/80
                    "
                >
                    <LiaFilterSolid className="w-4 h-4 text-base font-medium"/>
                    Фильтры
                </motion.div>
            </div>
            <div className="flex flex-col gap-3">
                {transactions.map((transaction, index) => (
                    <TransactionCard transaction={transaction} index={index}/>
                ))}
            </div>
        </div>
    )
}

export default Transactions;