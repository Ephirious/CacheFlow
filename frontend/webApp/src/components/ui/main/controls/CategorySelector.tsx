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
                    className={`flex items-center gap-2 px-4 py-3 rounded-2xl border cursor-pointer transition-all active:scale-95 ${
                        selectedId === category.id
                            ? "border-brand-primary ring-1 ring-brand-primary/20 bg-brand-primary/5 shadow-sm"
                            : "border-border-default bg-surface-base-soft hover:bg-surface-hover hover:border-border-strong"
                    }`}
                >
                    <span className={`flex whitespace-nowrap text-center font-medium ${selectedId === category.id ? "text-text-primary" : "text-text-label"}`}>
                        {category.emoji+" "}
                        {category.name}
                    </span>
                </button>
            ))}
            <button
                onClick={onAdd}
                className="flex text-text-primary items-center justify-center w-12 h-12 p-3 rounded-2xl border border-border-strong bg-surface-muted cursor-pointer transition-all hover:bg-surface-hover active:scale-95"
            >
                <IoAdd className="w-6 h-6"/>
            </button>
        </div>
    );
};

export default CategorySelector;
