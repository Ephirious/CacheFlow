import {FiCpu, FiSmile, FiShoppingCart, FiTruck} from "react-icons/fi";
import {LuShirt, LuUtensils} from "react-icons/lu";
import {AccountItem, CategoryItem, CategoryType, SettingsTab, SettingsTabOption} from "./types.ts";

export const settingsTabs: SettingsTabOption<SettingsTab>[] = [
    {id: "categories", label: "Категории"},
    {id: "accounts", label: "Счета"}
];

export const categoryTypeTabs: SettingsTabOption<CategoryType>[] = [
    {id: "expense", label: "Расходы"},
    {id: "income", label: "Доходы"}
];

export const categoryCards: CategoryItem[] = [
    {title: "Продукты", color: "text-category-groceries bg-category-groceries-soft", icon: <FiShoppingCart className="h-6 w-6"/>},
    {title: "Транспорт", color: "text-category-transport bg-category-transport-soft", icon: <FiTruck className="h-6 w-6"/>},
    {title: "Развлечения", color: "text-category-fun bg-category-fun-soft", icon: <FiSmile className="h-6 w-6"/>},
    {title: "Одежда", color: "text-category-clothes bg-category-clothes-soft", icon: <LuShirt className="h-6 w-6"/>},
    {title: "Еда", color: "text-category-food bg-category-food-soft", icon: <LuUtensils className="h-6 w-6"/>},
    {title: "Техника", color: "text-category-tech bg-category-tech-soft", icon: <FiCpu className="h-6 w-6"/>}
];

export const accounts: AccountItem[] = [
    {title: "Счёт1", balance: "100 000 ₽", color: "bg-account-palette-1"},
    {title: "Счёт2", balance: "100 000 ₽", color: "bg-account-palette-2"},
    {title: "Счёт3", balance: "100 000 ₽", color: "bg-account-palette-3"},
    {title: "Счёт4", balance: "100 000 ₽", color: "bg-account-palette-4"},
    {title: "Наличные", balance: "25 000 ₽", color: "bg-account-palette-5"},
    {title: "Сбер", balance: "150 000 ₽", color: "bg-account-palette-6"},
    {title: "Т-банк", balance: "80 000 ₽", color: "bg-account-palette-7"},
    {title: "Крипта", balance: "200 000 ₽", color: "bg-account-palette-8"}
];
