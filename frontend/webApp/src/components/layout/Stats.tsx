import {useMemo} from "react";
import {
    StatMetric,
    StatPeriod,
    StatPresetPeriod,
    StatsComponent,
    StatsIntent,
    StatsState
} from "k2ts";
import {
    CategoryPoint,
    StatsAccountOption,
    StatsChartsCarousel,
    StatsDateRange,
    StatsHero,
    StatsMetricCard,
    StatsMetricType,
    StatsPeriod as StatsPeriodUI
} from "../ui/stats";
import {useValue} from "interop";

const formatCurrency = (value: number, options?: { signed?: boolean }) => {
    const sign = options?.signed ? (value > 0 ? "+" : value < 0 ? "-" : "") : "";
    const abs = Math.abs(value);
    return `${sign}${new Intl.NumberFormat("ru-RU", {
        maximumFractionDigits: 2
    }).format(abs)} ₽`;
};

const bigDecimalToNumber = (value: { toString: () => string }) => Number(value.toString());

const toUiMetric = (metric: StatMetric): StatsMetricType => {
    if (metric === StatMetric.INCOME) return "income";
    if (metric === StatMetric.EXPENSE) return "expense";
    return "balance";
};

const toDomainMetric = (metric: StatsMetricType): StatMetric => {
    if (metric === "income") return StatMetric.INCOME;
    if (metric === "expense") return StatMetric.EXPENSE;
    return StatMetric.BALANCE;
};

const toUiPeriod = (period: StatPeriod): StatsPeriodUI => {
    if (period.preset === StatPresetPeriod.DAYS_30) return "30d";
    if (period.preset === StatPresetPeriod.MONTHS_3) return "3m";
    if (period.preset === StatPresetPeriod.MONTHS_6) return "6m";
    return "custom";
};

const toDomainPresetPeriod = (period: Exclude<StatsPeriodUI, "custom">): StatPresetPeriod => {
    if (period === "30d") return StatPresetPeriod.DAYS_30;
    if (period === "3m") return StatPresetPeriod.MONTHS_3;
    return StatPresetPeriod.MONTHS_6;
};

const formatPointLabel = (isoDate: string, byMonth: boolean) => {
    const date = new Date(isoDate);
    return date.toLocaleDateString("ru-RU", byMonth
        ? {month: "short"}
        : {day: "numeric", month: "short"});
};

const getDateRange = (state: StatsState): StatsDateRange => ({
    from: state.period.custom.from.toString(),
    to: state.period.custom.to.toString()
});

const shouldUseMonthlyLabels = (period: StatsPeriodUI, range: StatsDateRange): boolean => {
    if (period === "3m" || period === "6m") return true;
    if (period !== "custom") return false;
    const from = new Date(range.from);
    const to = new Date(range.to);
    const msInDay = 24 * 60 * 60 * 1000;
    return (to.getTime() - from.getTime()) / msInDay > 45;
};

const StatsConnected = ({component}: { component: StatsComponent }) => {
    const state = useValue(component.state);
    const uiPeriod = toUiPeriod(state.period);
    const dateRange = getDateRange(state);
    const useMonthlyLabels = shouldUseMonthlyLabels(uiPeriod, dateRange);

    const accountOptions = useMemo<ReadonlyArray<StatsAccountOption>>(
        () => [
            {label: "Все счета", value: "all"},
            ...state.accounts.asJsReadonlyArrayView().map((account) => ({
                value: account.id,
                label: account.title,
                balance: formatCurrency(bigDecimalToNumber(account.balance))
            }))
        ],
        [state.accounts]
    );

    const summaryCards = useMemo<ReadonlyArray<StatsMetricCard>>(
        () => [
            {
                title: "Доходы",
                value: formatCurrency(bigDecimalToNumber(state.summary.income), {signed: true}),
                positive: true
            },
            {
                title: "Расходы",
                value: formatCurrency(-bigDecimalToNumber(state.summary.expense), {signed: true}),
                positive: false
            },
            {
                title: "Чистый баланс",
                value: formatCurrency(bigDecimalToNumber(state.summary.net), {signed: true}),
                positive: !state.summary.net.isNegative
            },
        ],
        [state.summary]
    );

    const dynamics = useMemo(
        () => state.dynamics.asJsReadonlyArrayView().map((point) => ({
            label: formatPointLabel(point.timestamp.toString(), useMonthlyLabels),
            value: bigDecimalToNumber(point.value)
        })),
        [state.dynamics, useMonthlyLabels]
    );

    const expenseDistribution = useMemo<ReadonlyArray<CategoryPoint>>(
        () => state.expenseDistribution.asJsReadonlyArrayView().map((slice) => ({
            id: slice.categoryId,
            label: slice.categoryName,
            amount: formatCurrency(bigDecimalToNumber(slice.amount)),
            share: Number(slice.percent.toFixed(1)),
            color: slice.colorHex ?? undefined
        })),
        [state.expenseDistribution]
    );

    const currentBalance = useMemo(() => {
        const selected = state.accounts.asJsReadonlyArrayView().find((acc) => acc.id === state.selectedAccountId);
        if (selected) return formatCurrency(bigDecimalToNumber(selected.balance));
        const total = state.accounts.asJsReadonlyArrayView()
            .reduce((sum, account) => sum + bigDecimalToNumber(account.balance), 0);
        return formatCurrency(total);
    }, [state.accounts, state.selectedAccountId]);

    return (
        <div className="flex min-h-screen flex-col bg-surface-base pb-8">
            <StatsHero currentBalance={currentBalance}/>

            <div className="mx-auto flex w-full max-w-6xl flex-col gap-4 px-4 pt-4 sm:px-2 lg:pt-6">
                <StatsChartsCarousel
                    account={state.selectedAccountId ?? "all"}
                    accountOptions={accountOptions}
                    dateRange={dateRange}
                    dynamics={dynamics}
                    expenseDistribution={expenseDistribution}
                    metric={toUiMetric(state.metric)}
                    onAccountChange={(accountId) => {
                        component.intent(new StatsIntent.SelectAccount(accountId === "all" ? null : accountId));
                    }}
                    onDateRangeChange={(nextRange) => {
                        component.intent(new StatsIntent.SelectCustomPeriod(nextRange.from, nextRange.to));
                    }}
                    onMetricChange={(metric) => {
                        component.intent(new StatsIntent.SelectMetric(toDomainMetric(metric)));
                    }}
                    onPeriodChange={(period) => {
                        if (period === "custom") {
                            component.intent(new StatsIntent.SelectCustomPeriod(dateRange.from, dateRange.to));
                            return;
                        }
                        component.intent(new StatsIntent.SelectPresetPeriod(toDomainPresetPeriod(period)));
                    }}
                    period={uiPeriod}
                    summaryCards={summaryCards}
                />
            </div>
        </div>
    );
};

const Stats = ({component}: { component: StatsComponent }) => <StatsConnected component={component}/>;

export default Stats;
