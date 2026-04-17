import {CategoryItem, CategoryTypeId} from "../types.ts";
import {categoryTypeTabs} from "../data.tsx";
import {SegmentedTabs} from "../primitives";
import {CategoryCard} from "../display";
import { Category } from "k2ts";

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
                        {
                            id: item.id,
                            title: item.name,
                            // color: string; // TODO: Артём?
                            icon: item.emoji
                        }
                    } key={item.id} onClick={onCategoryClick}/>
                ))

                }
            </div>
        </div>
    );
};

export default CategoriesSection;
