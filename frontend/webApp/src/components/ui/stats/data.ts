import {
    CategoryPoint,
    StatsAccountOption,
    StatsAccountType,
    StatsChartPoint,
    StatsMetricCard,
    StatsMetricType,
    StatsPeriod,
    StatsPresetPeriod
} from "./types.ts";
import {accounts as settingsAccounts} from "../settings/data.tsx";

export const currentBalance = "1 183 400 ₽";

export const accountOptions: ReadonlyArray<StatsAccountOption> = [
    {label: "Все счета", value: "all"},
    ...settingsAccounts
        .filter((account) => !/^Счёт\d+$/i.test(account.title))
        .map((account) => ({
        value: account.id as StatsAccountType,
        label: account.title,
        balance: account.balance,
        colorClassName: account.color
    }))
];

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

export const summaryCards: ReadonlyArray<StatsMetricCard> = [
    {title: "Доходы", value: "+940 000", positive: true},
    {title: "Расходы", value: "-611 600", positive: false},
    {title: "Чистый баланс", value: "+328 400", positive: true},
];

export const chartDataByMetric: Record<StatsMetricType, Record<StatsPresetPeriod, ReadonlyArray<StatsChartPoint>>> = {
    income: {
        "30d": [
            {label: "1 фев.", dynamics: 12_000, monthly: 12_000},
            {label: "24 фев.", dynamics: 13_500, monthly: 12_300},
            {label: "2 мар.", dynamics: 16_200, monthly: 14_200},
            {label: "7 мар.", dynamics: 17_100, monthly: 14_800},
            {label: "9 мар.", dynamics: 16_900, monthly: 15_100},
            {label: "14 мар.", dynamics: 21_300, monthly: 16_900},
            {label: "15 мар.", dynamics: 34_000, monthly: 18_500}
        ],
        "3m": [
            {label: "Янв.", dynamics: 248_000, monthly: 228_000},
            {label: "Фев.", dynamics: 271_000, monthly: 249_000},
            {label: "Мар.", dynamics: 324_000, monthly: 292_000}
        ],
        "6m": [
            {label: "Окт.", dynamics: 210_000, monthly: 190_000},
            {label: "Нояб.", dynamics: 232_000, monthly: 211_000},
            {label: "Дек.", dynamics: 244_000, monthly: 225_000},
            {label: "Янв.", dynamics: 248_000, monthly: 228_000},
            {label: "Фев.", dynamics: 271_000, monthly: 249_000},
            {label: "Мар.", dynamics: 324_000, monthly: 292_000}
        ]
    },
    expense: {
        "30d": [
            {label: "1 фев.", dynamics: 10_300, monthly: 9_900},
            {label: "24 фев.", dynamics: 11_500, monthly: 10_800},
            {label: "2 мар.", dynamics: 12_600, monthly: 11_200},
            {label: "7 мар.", dynamics: 14_000, monthly: 12_300},
            {label: "9 мар.", dynamics: 13_800, monthly: 12_600},
            {label: "14 мар.", dynamics: 15_200, monthly: 13_900},
            {label: "15 мар.", dynamics: 17_400, monthly: 14_700}
        ],
        "3m": [
            {label: "Янв.", dynamics: 171_000, monthly: 162_000},
            {label: "Фев.", dynamics: 189_000, monthly: 176_000},
            {label: "Мар.", dynamics: 211_600, monthly: 198_200}
        ],
        "6m": [
            {label: "Окт.", dynamics: 138_000, monthly: 129_000},
            {label: "Нояб.", dynamics: 144_000, monthly: 135_000},
            {label: "Дек.", dynamics: 159_000, monthly: 149_000},
            {label: "Янв.", dynamics: 171_000, monthly: 162_000},
            {label: "Фев.", dynamics: 189_000, monthly: 176_000},
            {label: "Мар.", dynamics: 211_600, monthly: 198_200}
        ]
    },
    balance: {
        "30d": [
            {label: "1 фев.", dynamics: 1_200, monthly: 900},
            {label: "24 фев.", dynamics: 2_000, monthly: 1_300},
            {label: "2 мар.", dynamics: 3_600, monthly: 2_100},
            {label: "7 мар.", dynamics: 4_100, monthly: 2_500},
            {label: "9 мар.", dynamics: 3_100, monthly: 2_100},
            {label: "14 мар.", dynamics: 6_100, monthly: 3_100},
            {label: "15 мар.", dynamics: 34_000, monthly: 4_000}
        ],
        "3m": [
            {label: "Янв.", dynamics: 77_000, monthly: 66_000},
            {label: "Фев.", dynamics: 82_000, monthly: 73_000},
            {label: "Мар.", dynamics: 112_400, monthly: 93_800}
        ],
        "6m": [
            {label: "Окт.", dynamics: 72_000, monthly: 61_000},
            {label: "Нояб.", dynamics: 88_000, monthly: 76_000},
            {label: "Дек.", dynamics: 85_000, monthly: 76_000},
            {label: "Янв.", dynamics: 77_000, monthly: 66_000},
            {label: "Фев.", dynamics: 82_000, monthly: 73_000},
            {label: "Мар.", dynamics: 112_400, monthly: 93_800}
        ]
    }
};

export const categoryData: ReadonlyArray<CategoryPoint> = [
    {id: 1, label: "Техника", amount: "150 000 ₽", share: 24.5, color: "#C3114F"},
    {id: 2, label: "Еда", amount: "90 000 ₽", share: 14.7, color: "#3E7D68"},
    {id: 3, label: "Одежда", amount: "80 000 ₽", share: 13.1, color: "#5F9888"},
    {id: 4, label: "Развлечения", amount: "50 000 ₽", share: 8.2, color: "#8E5AE8"},
    {id: 5, label: "Транспорт", amount: "1 600 ₽", share: 0.3, color: "#F3A214"}
];

const MS_IN_DAY = 24 * 60 * 60 * 1000;

const getPresetByRangeLength = (dayCount: number): StatsPresetPeriod => {
    if (dayCount <= 45) return "30d";
    if (dayCount <= 120) return "3m";
    return "6m";
};

const formatDateLabel = (isoDate: string) =>
    new Date(isoDate).toLocaleDateString("ru-RU", {day: "numeric", month: "short"});

export const buildChartDataByRange = (
    metric: StatsMetricType,
    from: string,
    to: string
): ReadonlyArray<StatsChartPoint> => {
    if (!from || !to) return [];

    const fromDate = new Date(from);
    const toDate = new Date(to);

    if (Number.isNaN(fromDate.getTime()) || Number.isNaN(toDate.getTime()) || fromDate > toDate) {
        return [];
    }

    const totalDays = Math.max(Math.round((toDate.getTime() - fromDate.getTime()) / MS_IN_DAY), 1);
    const sampleSize = Math.min(Math.max(totalDays + 1, 2), 7);
    const source = chartDataByMetric[metric][getPresetByRangeLength(totalDays)];

    return Array.from({length: sampleSize}, (_, index) => {
        const progress = sampleSize === 1 ? 0 : index / (sampleSize - 1);
        const sourceIndex = Math.round(progress * (source.length - 1));
        const pointDate = new Date(fromDate.getTime() + Math.round(totalDays * progress) * MS_IN_DAY);
        const sourcePoint = source[sourceIndex];

        return {
            ...sourcePoint,
            label: formatDateLabel(pointDate.toISOString())
        };
    });
};
