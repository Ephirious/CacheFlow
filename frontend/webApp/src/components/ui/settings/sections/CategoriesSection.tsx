import {CategoryItem, CategoryTypeId} from "../types.ts";
import {categoryCards, categoryTypeTabs} from "../data.tsx";
import {SegmentedTabs} from "../primitives";
import {CategoryCard} from "../display";
import { Category } from "k2ts";
import transactions from "../../main/display/Transactions.tsx";

interface CategoriesSectionProps {
    categories: readonly Category[]
    categoryType: CategoryTypeId;
    onCategoryTypeChange: (type: CategoryTypeId) => void;
    onCategoryClick: (category: CategoryItem) => void;
}

const CategoriesSection = ({categories, categoryType, onCategoryTypeChange, onCategoryClick}: CategoriesSectionProps) => {
    return (
        <div className="flex flex-col gap-4">
            <SegmentedTabs active={categoryType} onChange={onCategoryTypeChange} options={categoryTypeTabs}/>
            <div className="grid grid-cols-2 gap-2">

                {categories.map((item) => (
                    <CategoryCard item={
                        new CategoryItem()
                    } key={item.id} onClick={onCategoryClick}/>
                ))

                }
            </div>
        </div>
    );
};

export default CategoriesSection;
