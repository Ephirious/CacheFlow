import {AccountItem, CategoryTypeId, SettingsTab, SettingsTabOption} from "./types.ts";

export const settingsTabs: SettingsTabOption<SettingsTab>[] = [
    {id: "categories", label: "Категории"},
    {id: "accounts", label: "Счета"}
];

export const categoryTypeTabs: SettingsTabOption<CategoryTypeId>[] = [
    {id: "outcome", label: "Расход"},
    {id: "income", label: "Доход"}
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
    "#4A86F7",
    "#8B5CF6",
    "#E45796",
    "#F59E0B",
    "#66BB6A",
    "#6366F1",
    "#EAB308",
    "#EF4444",
    "#F39C12",
    "#52B1A2"
];
