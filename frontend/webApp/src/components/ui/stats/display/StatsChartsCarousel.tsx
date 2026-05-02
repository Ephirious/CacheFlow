import {periodTabs} from "../data.ts";
import {SegmentedControl} from "../primitives";
import AccountSelector from "../sections/AccountSelector.tsx";
import CombinedStatsChart from "./CombinedStatsChart.tsx";
import ExpenseDistribution from "./ExpenseDistribution.tsx";
import SummaryCards from "./SummaryCards.tsx";
import {StatsAccountOption, StatsAccountType, StatsDateRange, StatsMetricType, StatsPeriod} from "../types.ts";

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
    onDateRangeChange
}: StatsChartsCarouselProps) => {
    return (
        <div className="flex flex-col rounded-3xl gap-4">
            <section className="rounded-3xl bg-surface-base p-4 shadow-sm">
                <h2 className="text-base font-bold text-text-primary">Фильтры</h2>
                <div className="mt-3 flex flex-col gap-2">
                    <p className="text-xs font-semibold uppercase tracking-wide text-text-secondary">Период</p>
                    <SegmentedControl onChange={onPeriodChange} options={periodTabs} value={period}/>
                </div>
                <div className="mt-2 flex flex-wrap items-end gap-3">
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

            <SummaryCards/>
            <CombinedStatsChart
                dateRange={dateRange}
                metric={metric}
                onMetricChange={onMetricChange}
                period={period}
            />
            <ExpenseDistribution/>
        </div>
    );
};

export default StatsChartsCarousel;
