import {StatsMetricType, StatsPeriod} from "./types.ts";

export const metricTabs: ReadonlyArray<{ label: string; value: StatsMetricType }> = [
    {label: "Доходы", value: "income"},
    {label: "Расходы", value: "expense"},
    {label: "Баланс", value: "balance"}
];

export const periodTabs: ReadonlyArray<{ label: string; value: StatsPeriod }> = [
    {label: "30 дней", value: "30d"},
    {label: "3 мес.", value: "3m"},
    {label: "6 мес.", value: "6m"},
    {label: "Свой", value: "custom"}
];
