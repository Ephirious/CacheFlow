import {periodTabs} from "../data.ts";
import {SegmentedControl} from "../primitives";
import AccountSelector from "../sections/AccountSelector.tsx";
import CombinedStatsChart from "./CombinedStatsChart.tsx";
import ExpenseDistribution from "./ExpenseDistribution.tsx";
import SummaryCards from "./SummaryCards.tsx";
import {
    CategoryPoint,
    StatsAccountOption,
    StatsAccountType,
    StatsChartPoint,
    StatsDateRange,
    StatsMetricCard,
    StatsMetricType,
    StatsPeriod
} from "../types.ts";

interface StatsChartsCarouselProps {
    account: StatsAccountType;
    accountOptions: ReadonlyArray<StatsAccountOption>;
    period: StatsPeriod;
    metric: StatsMetricType;
    dateRange: StatsDateRange;
    onAccountChange: (account: StatsAccountType) => void;
    onMetricChange: (metric: StatsMetricType) => void;
    onPeriodChange: (period: StatsPeriod) => void;
    onDateRangeChange: (nextRange: StatsDateRange) => void;
    summaryCards: ReadonlyArray<StatsMetricCard>;
    dynamics: ReadonlyArray<StatsChartPoint>;
    expenseDistribution: ReadonlyArray<CategoryPoint>;
}

const StatsChartsCarousel = ({
    account,
    accountOptions,
    period,
    metric,
    dateRange,
    onAccountChange,
    onMetricChange,
    onPeriodChange,
    onDateRangeChange,
    summaryCards,
    dynamics,
    expenseDistribution
}: StatsChartsCarouselProps) => {
    return (
        <div className="flex flex-col gap-4 lg:gap-5">
            <section className="rounded-3xl bg-surface-sheet p-4 shadow-sm lg:p-5">
                <h2 className="text-base font-bold text-text-primary">Фильтры</h2>
                <div className="mt-3 flex flex-col gap-3">
                    <div >
                        <p className="text-xs mb-2 font-semibold uppercase tracking-wide text-text-secondary">Период</p>
                        <SegmentedControl onChange={onPeriodChange} options={periodTabs} value={period}/>
                    </div>
                </div>

                <div className="mt-3 flex flex-wrap items-end gap-3">
                    {period === "custom" && (
                        <>
                            <label className="flex flex-col gap-1 text-xs text-text-secondary">
                                От
                                <input
                                    className="rounded-lg border border-border-subtle bg-surface-base px-3 py-1.5 text-sm text-text-primary"
                                    max={dateRange.to || undefined}
                                    onChange={(event) => onDateRangeChange({...dateRange, from: event.target.value})}
                                    type="date"
                                    value={dateRange.from}
                                />
                            </label>
                            <label className="flex flex-col gap-1 text-xs text-text-secondary">
                                До
                                <input
                                    className="rounded-lg border border-border-subtle bg-surface-base px-3 py-1.5 text-sm text-text-primary"
                                    min={dateRange.from || undefined}
                                    onChange={(event) => onDateRangeChange({...dateRange, to: event.target.value})}
                                    type="date"
                                    value={dateRange.to}
                                />
                            </label>
                        </>
                    )}
                </div>

                <div className="mt-3">
                    <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-text-secondary">Счёт</p>
                    <AccountSelector account={account} onChange={onAccountChange} options={accountOptions}/>
                </div>
            </section>

            <SummaryCards cards={summaryCards}/>

            <div className="grid gap-4 lg:grid-cols-12 lg:gap-5">
                <div className="lg:col-span-8">
                    <CombinedStatsChart
                        metric={metric}
                        onMetricChange={onMetricChange}
                        points={dynamics}
                    />
                </div>
                <div className="lg:col-span-4">
                    <ExpenseDistribution categories={expenseDistribution}/>
                </div>
            </div>
        </div>
    );
};

export default StatsChartsCarousel;
