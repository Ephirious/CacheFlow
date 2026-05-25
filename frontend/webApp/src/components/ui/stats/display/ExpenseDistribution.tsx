import {Cell, Pie, PieChart, ResponsiveContainer} from "recharts";
import {CategoryPoint} from "../types.ts";

interface ExpenseDistributionProps {
    categories: ReadonlyArray<CategoryPoint>;
}

const ExpenseDistribution = ({categories}: ExpenseDistributionProps) => {
    const hasCategories = categories.length > 0;

    return (
        <section className="flex h-full flex-col gap-3 rounded-3xl bg-surface-sheet p-4 shadow-sm lg:p-5">
            <h2 className="text-xl font-bold text-text-primary">Расходы</h2>
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
                                    stroke="var(--color-surface-sheet)"
                                    strokeWidth={4}
                                >
                                    {categories.map((entry) => (
                                        <Cell key={entry.id} fill={entry.color ?? "var(--color-border-default)"}/>
                                    ))}
                                </Pie>
                            </PieChart>
                        </ResponsiveContainer>
                    </div>

                    <div className="mt-2 grid grid-cols-1 gap-y-2 sm:grid-cols-2 sm:gap-x-3 sm:gap-y-1.5">
                        {categories.map((category) => (
                            <div className="flex items-center justify-between gap-2 text-xs text-text-label sm:text-sm" key={category.id}>
                                <span className="flex items-center gap-2">
                                    <span className="h-2.5 w-2.5 rounded-full" style={{backgroundColor: category.color ?? "var(--color-border-default)"}}/>
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
