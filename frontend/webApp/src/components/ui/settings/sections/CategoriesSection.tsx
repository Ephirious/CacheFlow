import {CategoryType} from "../types.ts";
import {categoryCards, categoryTypeTabs} from "../data.tsx";
import {SegmentedTabs} from "../primitives";
import {CategoryCard} from "../display";

interface CategoriesSectionProps {
    categoryType: CategoryType;
    onCategoryTypeChange: (type: CategoryType) => void;
}

const CategoriesSection = ({categoryType, onCategoryTypeChange}: CategoriesSectionProps) => {
    return (
        <div className="flex flex-col gap-4">
            <SegmentedTabs active={categoryType} onChange={onCategoryTypeChange} options={categoryTypeTabs}/>
            <div className="grid grid-cols-2 gap-2">
                {categoryCards.map((item) => (
                    <CategoryCard key={item.title} item={item}/>
                ))}
            </div>
        </div>
    );
};

export default CategoriesSection;
