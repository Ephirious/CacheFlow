export type StatsPresetPeriod = "30d" | "3m" | "6m";
export type StatsPeriod = StatsPresetPeriod | "custom";
export type StatsMetricType = "income" | "expense" | "balance";
export type StatsAccountType = string;

export interface StatsAccountOption {
    value: StatsAccountType;
    label: string;
    balance?: string;
    colorClassName?: string;
}

export interface StatsDateRange {
    from: string;
    to: string;
}

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
