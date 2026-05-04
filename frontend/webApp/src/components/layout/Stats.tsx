import {useState} from "react";
import {
    AccountSelector,
    CombinedStatsChart,
    ExpenseDistribution,
    StatsAccountType,
    StatsHero,
    StatsMetricType,
    StatsPeriod,
    SummaryCards,
    TopCategories
} from "../ui/stats";

const Stats = () => {
    const [account, setAccount] = useState<StatsAccountType>("all");
    const [metric, setMetric] = useState<StatsMetricType>("balance");
    const [period, setPeriod] = useState<StatsPeriod>("30d");

    return (
        <div className="flex min-h-screen flex-col bg-surface-subtle md:pb-8">
            <StatsHero/>

            <div className="flex p-6 w-full max-w-5xl flex-col gap-3">
                <AccountSelector account={account} onChange={setAccount}/>
                <SummaryCards/>
                <CombinedStatsChart
                    period={period}
                    metric={metric}
                    onMetricChange={setMetric}
                    onPeriodChange={setPeriod}
                />
                <ExpenseDistribution/>
                <TopCategories/>
            </div>
        </div>
    );
};

export default Stats;
