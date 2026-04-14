import {CategoryItem} from "../types.ts";

const CategoryCard = ({item}: { item: CategoryItem }) => {
    return (
        <button
            className="flex flex-col items-center gap-2 rounded-xl border border-border-strong bg-surface-base py-2"
            type="button"
        >
            <div className={`flex rounded-xl p-3 ${item.color}`}>
                {item.icon}
            </div>
            <p className="text-sm font-semibold">{item.title}</p>
        </button>
    );
};

export default CategoryCard;
