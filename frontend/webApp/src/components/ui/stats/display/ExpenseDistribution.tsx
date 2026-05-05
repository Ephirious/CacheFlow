import {Cell, Pie, PieChart, ResponsiveContainer} from "recharts";
import {CategoryPoint} from "../types.ts";

interface ExpenseDistributionProps {
    categories: ReadonlyArray<CategoryPoint>;
}

const ExpenseDistribution = ({categories}: ExpenseDistributionProps) => {
    const hasCategories = categories.length > 0;

    return (
        <section className="flex flex-col h-full rounded-3xl bg-surface-sheet p-3 shadow-sm gap-3">
            <div className="flex justify-center">
                <h2 className="text-xl font-bold text-text-primary sm:text-3xl">Расходы</h2>
            </div>
            {hasCategories ? (
                <>
                    <div className="h-48 w-full">
                        <ResponsiveContainer>
                            <PieChart>
                                <Pie
                                    data={[...categories]}
                                    dataKey="share"
                                    nameKey="label"
                                    innerRadius={55}
                                    outerRadius={88}
                                    stroke="#FFFFFF"
                                    strokeWidth={4}
                                >
                                    {categories.map((entry) => (
                                        <Cell key={entry.id} fill={entry.color ?? "#CBD5E1"}/>
                                    ))}
                                </Pie>
                            </PieChart>
                        </ResponsiveContainer>
                    </div>

                    <div className="mt-2 grid grid-cols-1 gap-y-2 sm:grid-cols-2 sm:gap-x-3 sm:gap-y-1.5">
                        {categories.map((category) => (
                            <div className="flex items-center justify-between gap-2 text-xs text-text-label sm:text-sm" key={category.id}>
                                <span className="flex items-center gap-2">
                                    <span className="h-2.5 w-2.5 rounded-full" style={{backgroundColor: category.color ?? "#CBD5E1"}}/>
                                    {category.label}
                                </span>
                                <span className="font-semibold text-text-secondary">{category.share}%</span>
                            </div>
                        ))}
                    </div>
                </>
            ) : (
                <div className="flex h-48 items-center justify-center rounded-2xl bg-surface-muted px-4 text-center text-sm text-text-secondary">
                    Нет данных по расходам за выбранный период
                </div>
            )}
        </section>
    );
};

export default ExpenseDistribution;
