import {Cell, Pie, PieChart, ResponsiveContainer} from "recharts";
import {categoryData} from "../data.ts";



const ExpenseDistribution = () => {
    return (
        <section className="flex flex-col h-full rounded-3xl bg-surface-base p-3 shadow-sm gap-3">
            <div className="flex justify-center">
                <h2 className="text-xl font-bold text-text-primary sm:text-3xl">Распределение</h2>
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

            <div className="mt-2 grid grid-cols-1 gap-y-2 sm:grid-cols-2 sm:gap-x-3 sm:gap-y-1.5">
                {categoryData.map((category) => (
                    <div className="flex items-center justify-between gap-2 text-xs text-text-label sm:text-sm" key={category.id}>
                        <span className="flex items-center gap-2">
                            <span className="h-2.5 w-2.5 rounded-full" style={{backgroundColor: category.color}}/>
                            {category.label}
                        </span>
                        <span className="font-semibold text-text-secondary">{category.share}%</span>
                    </div>
                ))}
            </div>
        </section>
    );
};

export default ExpenseDistribution;
