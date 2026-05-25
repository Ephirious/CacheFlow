import {
    CartesianGrid,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";
import {metricTabs} from "../data.ts";
import {SegmentedControl} from "../primitives";
import {StatsChartPoint, StatsMetricType} from "../types.ts";

interface CombinedStatsChartProps {
    metric: StatsMetricType;
    points: ReadonlyArray<StatsChartPoint>;
    onMetricChange: (metric: StatsMetricType) => void;
}

const CombinedStatsChart = ({
    metric,
    points,
    onMetricChange,
}: CombinedStatsChartProps) => {
    return (
        <div className="flex h-full w-full flex-col gap-4 rounded-3xl bg-surface-sheet p-4 shadow-sm lg:p-5">
            <h2 className="text-xl font-bold text-text-primary">Динамика</h2>

            <div className="flex flex-wrap gap-2 justify-center">
                <SegmentedControl value={metric} options={metricTabs} onChange={onMetricChange}/>
            </div>

            <div className="mb-2 flex items-center gap-3 text-xs text-text-secondary">
                <span className="flex items-center gap-1.5">
                    <span className="h-2.5 w-2.5 rounded-full bg-brand-primary"/>
                    Динамика
                </span>
            </div>

            <div className="h-56 w-full lg:h-72">
                <ResponsiveContainer>
                    <LineChart data={[...points]} margin={{top: 12, right: 8, left: -16, bottom: 0}}>
                        <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border-strong)" vertical={false}/>
                        <XAxis axisLine={false} dataKey="label" tickLine={false}
                               tick={{fontSize: 12, fill: "var(--color-text-secondary)"}}/>
                        <YAxis
                            axisLine={false}
                            tickLine={false}
                            tick={{fontSize: 12, fill: "var(--color-text-secondary)"}}
                            width={70}
                        />
                        <Tooltip
                            cursor={{fill: "var(--color-state-brand-soft)"}}
                            contentStyle={{
                                borderRadius: "12px",
                                border: "1px solid var(--color-border-strong)",
                                backgroundColor: "var(--color-surface-base)",
                                boxShadow: "0 10px 24px rgba(15, 23, 42, 0.16)"
                            }}
                            itemStyle={{color: "var(--color-text-primary)"}}
                            formatter={(value: number) => [`${value.toLocaleString("ru-RU")} ₽`, "Динамика"]}
                            labelStyle={{color: "var(--color-text-primary)", fontWeight: 600}}
                        />
                        <Line
                            dataKey="value"
                            stroke="var(--color-brand-primary)"
                            strokeWidth={3}
                            dot={{r: 4, fill: "var(--color-brand-primary)", stroke: "var(--color-surface-base)", strokeWidth: 2}}
                            activeDot={{r: 5}}
                            type="monotone"
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default CombinedStatsChart;
