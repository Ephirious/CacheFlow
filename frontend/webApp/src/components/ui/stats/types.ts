export type StatsPeriod = "30d" | "3m" | "6m";
export type StatsMetricType = "income" | "expense" | "balance";
export type StatsAccountType = "all" | "account-1" | "account-2";

export interface StatsChartPoint {
    label: string;
    dynamics: number;
    monthly: number;
}

export interface StatsMetricCard {
    title: string;
    value: string;
    positive: boolean;
}

export interface CategoryPoint {
    id: number;
    label: string;
    amount: string;
    share: number;
    color: string;
}
