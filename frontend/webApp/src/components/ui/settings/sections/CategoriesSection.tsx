import {CategoryItem, CategoryType} from "../types.ts";
import {categoryCards, categoryTypeTabs} from "../data.tsx";
import {SegmentedTabs} from "../primitives";
import {CategoryCard} from "../display";

interface CategoriesSectionProps {
    categoryType: CategoryType;
    onCategoryTypeChange: (type: CategoryType) => void;
    onCategoryClick: (category: CategoryItem) => void;
}

const CategoriesSection = ({categoryType, onCategoryTypeChange, onCategoryClick}: CategoriesSectionProps) => {
    return (
        <div className="flex flex-col gap-4">
            <SegmentedTabs active={categoryType} onChange={onCategoryTypeChange} options={categoryTypeTabs}/>
            <div className="grid grid-cols-2 gap-2">
                {categoryCards.map((item) => (
                    <CategoryCard item={item} key={item.id} onClick={onCategoryClick}/>
                ))}
            </div>
        </div>
    );
};

export default CategoriesSection;
