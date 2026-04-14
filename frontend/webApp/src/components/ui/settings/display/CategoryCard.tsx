import {CategoryItem} from "../types.ts";

interface CategoryCardProps {
    item: CategoryItem;
    onClick: (item: CategoryItem) => void;
}

const CategoryCard = ({item, onClick}: CategoryCardProps) => {
    return (
        <button
            className="flex flex-col items-center gap-2 rounded-xl border border-border-strong bg-surface-base py-2"
            onClick={() => onClick(item)}
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
