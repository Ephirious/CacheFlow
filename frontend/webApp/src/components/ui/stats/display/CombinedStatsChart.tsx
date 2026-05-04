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
        <div className="flex w-full flex-col h-full rounded-3xl bg-surface-sheet p-4 shadow-sm gap-4">
            <div className="flex justify-center">
                <h2 className="flex text-xl font-bold text-center text-text-primary">Динамика</h2>
            </div>

            <div className="flex flex-wrap gap-2 justify-center">
                <SegmentedControl value={metric} options={metricTabs} onChange={onMetricChange}/>
            </div>

            <div className="mb-2 flex items-center gap-3 text-xs text-text-secondary">
                <span className="flex items-center gap-1.5">
                    <span className="h-2.5 w-2.5 rounded-full bg-brand-primary"/>
                    Динамика
                </span>
            </div>

            <div className="h-56 w-full">
                <ResponsiveContainer>
                    <LineChart data={[...points]} margin={{top: 12, right: 8, left: -16, bottom: 0}}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" vertical={false}/>
                        <XAxis axisLine={false} dataKey="label" tickLine={false}
                               tick={{fontSize: 12, fill: "#64748B"}}/>
                        <YAxis axisLine={false} tickLine={false} tick={{fontSize: 12, fill: "#64748B"}} width={70}/>
                        <Tooltip
                            cursor={{fill: "rgba(79,57,246,0.08)"}}
                            contentStyle={{
                                borderRadius: "12px",
                                border: "1px solid #E5E7EB",
                                boxShadow: "0 10px 24px rgba(15, 23, 42, 0.06)"
                            }}
                            formatter={(value: number) => [`${value.toLocaleString("ru-RU")} ₽`, "Динамика"]}
                            labelStyle={{color: "#0F172A", fontWeight: 600}}
                        />
                        <Line
                            dataKey="value"
                            stroke="#4F39F6"
                            strokeWidth={3}
                            dot={{r: 4, fill: "#4F39F6", stroke: "#ffffff", strokeWidth: 2}}
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
