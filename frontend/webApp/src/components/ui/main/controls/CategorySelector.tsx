import {Category} from "k2ts";
import {IoAdd} from "react-icons/io5";

interface CategorySelectorProps {
    categories: readonly Category[];
    selectedId: string | null;
    onSelect: (id: string) => void;
    onAdd: () => void;
}

const CategorySelector = ({
                              categories,
                              selectedId,
                              onSelect,
                              onAdd
                          }: CategorySelectorProps) => {
    return (
        <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide overflow-hidden">
            {categories.map((category) => (
                <button
                    key={category.id}
                    onClick={() => onSelect(category.id)}
                    className={`flex items-center bg-surface-muted gap-2 px-4 py-3 rounded-2xl border transition-all ${
                        selectedId === category.id
                            ? "bg-surface-base border-border-default shadow-sm"
                            : "bg-surface-base border-border-strong"
                    }`}
                >
                    <span className="flex whitespace-nowrap text-center font-medium text-text-label">
                        {category.emoji+" "}
                        {category.name}
                    </span>
                </button>
            ))}
            <button
                onClick={onAdd}
                className="flex text-text-primary items-center justify-center w-12 h-12 p-3 rounded-2xl border border-border-strong bg-surface-muted hover:bg-surface-hover transition-colors"
            >
                <IoAdd className="w-6 h-6"/>
            </button>
        </div>
    );
};

export default CategorySelector;
