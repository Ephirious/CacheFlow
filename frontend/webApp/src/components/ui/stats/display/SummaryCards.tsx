import {FiArrowDownCircle, FiArrowUpCircle, FiTrendingUp} from "react-icons/fi";
import {summaryCards} from "../data.ts";

const SummaryCards = () => {
    return (
        <div className="grid grid-cols-2 gap-4">
            {summaryCards.map((card, index) => {
                const isIncome = index === 0;
                const isExpense = index === 1;
                const icon = isIncome ? (
                    <FiArrowUpCircle className="h-4 w-4"/>
                ) : isExpense ? (
                    <FiArrowDownCircle className="h-4 w-4"/>
                ) : (
                    <FiTrendingUp className="h-4 w-4"/>
                );

                return (
                    <div
                        className={`rounded-2xl bg-white p-3 ${index === 2 ? "col-span-full" : ""}`}
                        key={card.title}
                    >
                        <div
                            className={`mb-2 inline-flex rounded-xl p-1.5 sm:mb-3 sm:p-2 ${
                                card.positive ? "bg-state-success-soft text-state-success" : "bg-state-danger-soft text-state-danger"
                            }`}
                        >
                            {icon}
                        </div>
                        <p className="text-xs text-text-secondary">{card.title}</p>
                        <p className={`mt-1 text-3xl font-bold sm:text-4xl ${card.positive ? "text-state-success" : "text-state-danger"}`}>
                            {card.value}
                        </p>
                    </div>
                );
            })}
        </div>
    );
};

export default SummaryCards;
