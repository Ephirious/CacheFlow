import {useEffect, useMemo, useRef, useState} from "react";
import {
    StatsAccountOption,
    StatsAccountType,
    StatsChartsCarousel,
    StatsDateRange,
    StatsHero,
    StatsMetricType,
    StatsPeriod
} from "../ui/stats";
import {accountOptions as fallbackAccountOptions} from "../ui/stats/data.ts";
import {MainComponent} from "k2ts";
import {useValue} from "interop";

const toDateInputValue = (date: Date) => date.toISOString().slice(0, 10);

interface StatsScreenProps {
    accountOptions: ReadonlyArray<StatsAccountOption>;
}

const StatsScreen = ({accountOptions}: StatsScreenProps) => {
    const [account, setAccount] = useState<StatsAccountType>("all");
    const [metric, setMetric] = useState<StatsMetricType>("balance");
    const [period, setPeriod] = useState<StatsPeriod>("30d");
    const [dateRange, setDateRange] = useState<StatsDateRange>(() => {
        const to = new Date();
        const from = new Date();
        from.setDate(from.getDate() - 29);
        return {from: toDateInputValue(from), to: toDateInputValue(to)};
    });

    useEffect(() => {
        if (!accountOptions.some((option) => option.value === account)) {
            setAccount("all");
        }
    }, [accountOptions, account]);
    return (
        <div className="flex min-h-screen flex-col bg-surface-base md:pb-8">
            <StatsHero/>

            <div className="flex p-6 w-full max-w-5xl flex-col gap-3">
                <StatsChartsCarousel
                    account={account}
                    accountOptions={accountOptions}
                    dateRange={dateRange}
                    metric={metric}
                    onAccountChange={setAccount}
                    onDateRangeChange={setDateRange}
                    onMetricChange={setMetric}
                    onPeriodChange={setPeriod}
                    period={period}
                />
            </div>
        </div>
    );
};

const StatsConnected = ({mainComponent}: { mainComponent: MainComponent }) => {
    const summaryState = useValue(mainComponent.summaryComponent.state);
    const lastNonEmptyAccountOptionsRef = useRef<ReadonlyArray<StatsAccountOption>>(fallbackAccountOptions);
    const accountOptions = useMemo<ReadonlyArray<StatsAccountOption>>(
        () => {
            const liveAccounts = summaryState.accounts.asJsReadonlyArrayView();
            if (liveAccounts.length === 0) {
                return lastNonEmptyAccountOptionsRef.current;
            }

            const nextOptions: ReadonlyArray<StatsAccountOption> = [
                {label: "Все счета", value: "all"},
                ...liveAccounts.map((acc) => ({
                    value: acc.id,
                    label: acc.title,
                    balance: `${acc.balance.prettyString()} ₽`
                }))
            ];
            lastNonEmptyAccountOptionsRef.current = nextOptions;
            return nextOptions;
        },
        [summaryState]
    );
    return <StatsScreen accountOptions={accountOptions}/>;
};

const Stats = ({mainComponent}: { mainComponent?: MainComponent }) =>
    mainComponent ? <StatsConnected mainComponent={mainComponent}/> : <StatsScreen accountOptions={fallbackAccountOptions}/>;

export default Stats;
