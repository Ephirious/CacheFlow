import {categoryData} from "../data.ts";

const TopCategories = () => {
    return (
        <section className="rounded-3xl bg-surface-base p-3 shadow-sm sm:p-5">
            <h2 className="mb-3 text-xl font-bold text-text-primary sm:mb-4 sm:text-3xl">Топ-5 категорий расходов</h2>

            <div className="space-y-2.5 sm:space-y-3">
                {categoryData.map((category) => (
                    <article className="rounded-2xl bg-surface-base p-3 ring-1 ring-border-subtle sm:p-3.5" key={category.id}>
                        <div className="flex items-start gap-3">
                            <span
                                className="inline-flex h-7 w-7 items-center justify-center rounded-lg text-xs font-bold text-white sm:h-8 sm:w-8 sm:rounded-xl sm:text-sm"
                                style={{backgroundColor: category.color}}
                            >
                                {category.id}
                            </span>
                            <div className="min-w-0 flex-1">
                                <p className="text-sm font-semibold text-text-primary sm:text-base">{category.label}</p>
                                <p className="text-2xl font-bold text-text-primary sm:text-4xl">{category.amount}</p>
                            </div>
                        </div>
                        <div className="mt-2.5 h-2 rounded-full bg-border-strong sm:mt-3">
                            <div
                                className="h-2 rounded-full"
                                style={{backgroundColor: category.color, width: `${Math.min(category.share * 2.2, 100)}%`}}
                            />
                        </div>
                        <p className="mt-1.5 text-xs font-semibold sm:mt-2 sm:text-sm" style={{color: category.color}}>
                            {category.share}% от всех расходов
                        </p>
                    </article>
                ))}
            </div>
        </section>
    );
};

export default TopCategories;
