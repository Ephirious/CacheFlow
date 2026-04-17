import {PiClockClockwise} from "react-icons/pi";
import {Cell, Pie, PieChart, ResponsiveContainer} from "recharts";
import {categoryData} from "../data.ts";

const ExpenseDistribution = () => {
    return (
        <section className="rounded-3xl bg-surface-base p-3 shadow-sm">
            <div className="mb-2.5 flex items-center gap-2">
                <div className="rounded-xl bg-brand-primary/10 p-1.5 text-brand-primary">
                    <PiClockClockwise className="h-3.5 w-3.5"/>
                </div>
                <h2 className="text-xl font-bold text-text-primary sm:text-3xl">Распределение расходов</h2>
            </div>

            <div className="h-48 w-full">
                <ResponsiveContainer>
                    <PieChart>
                        <Pie
                            data={[...categoryData]}
                            dataKey="share"
                            nameKey="label"
                            innerRadius={55}
                            outerRadius={88}
                            stroke="#FFFFFF"
                            strokeWidth={4}
                        >
                            {categoryData.map((entry) => (
                                <Cell key={entry.id} fill={entry.color}/>
                            ))}
                        </Pie>
                    </PieChart>
                </ResponsiveContainer>
            </div>

            <div className="mt-2 grid grid-cols-2 gap-x-3 gap-y-1.5">
                {categoryData.map((category) => (
                    <div className="flex items-center gap-2 text-xs text-text-label sm:text-sm" key={category.id}>
                        <span className="h-2.5 w-2.5 rounded-full" style={{backgroundColor: category.color}}/>
                        {category.label}
                    </div>
                ))}
            </div>
        </section>
    );
};

export default ExpenseDistribution;
