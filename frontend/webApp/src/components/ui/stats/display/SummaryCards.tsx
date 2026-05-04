import {FiArrowDownCircle, FiArrowUpCircle, FiTrendingUp} from "react-icons/fi";
import {StatsMetricCard} from "../types.ts";

interface SummaryCardsProps {
    cards: ReadonlyArray<StatsMetricCard>;
}

const SummaryCards = ({cards}: SummaryCardsProps) => {
    return (
        <div className="flex flex-col gap-3 w-full h-full rounded-3xl bg-surface-base p-4 shadow-sm">
            <div className="flex justify-center">
                <h2 className="flex text-xl font-bold text-center">Баланс</h2>
            </div>
            {cards.map((card, index) => {
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
                        className="rounded-2xl bg-white p-3"
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
                        <p className={`mt-1 whitespace-nowrap text-2xl font-bold sm:text-3xl ${card.positive ? "text-state-success" : "text-state-danger"}`}>
                            {card.value}
                        </p>
                    </div>
                );
            })}
        </div>
    );
};

export default SummaryCards;
