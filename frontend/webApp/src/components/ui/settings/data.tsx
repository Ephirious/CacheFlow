import {FiCpu, FiSmile, FiShoppingCart, FiTruck} from "react-icons/fi";
import {LuShirt, LuUtensils} from "react-icons/lu";
import {AccountItem, CategoryItem, CategoryTypeId, SettingsTab, SettingsTabOption} from "./types.ts";

export const settingsTabs: SettingsTabOption<SettingsTab>[] = [
    {id: "categories", label: "Категории"},
    {id: "accounts", label: "Счета"}
];

export const categoryTypeTabs: SettingsTabOption<CategoryTypeId>[] = [
    {id: "outcome", label: "Расход"},
    {id: "income", label: "Доход"}
];

export const categoryCards: CategoryItem[] = [
    {id: "groceries", title: "Продукты", color: "text-category-groceries bg-category-groceries-soft", icon: <FiShoppingCart className="h-6 w-6"/>},
    {id: "transport", title: "Транспорт", color: "text-category-transport bg-category-transport-soft", icon: <FiTruck className="h-6 w-6"/>},
    {id: "fun", title: "Развлечения", color: "text-category-fun bg-category-fun-soft", icon: <FiSmile className="h-6 w-6"/>},
    {id: "clothes", title: "Одежда", color: "text-category-clothes bg-category-clothes-soft", icon: <LuShirt className="h-6 w-6"/>},
    {id: "food", title: "Еда", color: "text-category-food bg-category-food-soft", icon: <LuUtensils className="h-6 w-6"/>},
    {id: "tech", title: "Техника", color: "text-category-tech bg-category-tech-soft", icon: <FiCpu className="h-6 w-6"/>}
];

export const accounts: AccountItem[] = [
    {id: "account-1", title: "Счёт1", balance: "100 000 ₽", color: "bg-account-palette-1"},
    {id: "account-2", title: "Счёт2", balance: "100 000 ₽", color: "bg-account-palette-2"},
    {id: "account-3", title: "Счёт3", balance: "100 000 ₽", color: "bg-account-palette-3"},
    {id: "account-4", title: "Счёт4", balance: "100 000 ₽", color: "bg-account-palette-4"},
    {id: "cash", title: "Наличные", balance: "25 000 ₽", color: "bg-account-palette-5"},
    {id: "sber", title: "Сбер", balance: "150 000 ₽", color: "bg-account-palette-6"},
    {id: "tbank", title: "Т-банк", balance: "80 000 ₽", color: "bg-account-palette-7"},
    {id: "crypto", title: "Крипта", balance: "200 000 ₽", color: "bg-account-palette-8"}
];

export const accountColorOptions = [
    "bg-account-palette-1",
    "bg-account-palette-2",
    "bg-account-palette-3",
    "bg-account-palette-5",
    "bg-account-palette-6",
    "bg-account-palette-8",
    "bg-account-palette-7",
    "bg-category-groceries",
    "bg-category-transport",
    "bg-category-food"
];
