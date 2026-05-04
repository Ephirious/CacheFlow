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
            <div className={`flex rounded-2xl w-12 h-12 p-3 text-category-groceries bg-indigo-100 justify-center`}> {/* TODO: Артём? ${item.color} */}
                {item.icon}
            </div>
            <p className="text-sm font-semibold">{item.title}</p>
        </button>
    );
};

export default CategoryCard;
