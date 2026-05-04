export type SettingsTab = "categories" | "accounts";
export type CategoryTypeId = "outcome" | "income";

export interface SettingsTabOption<T extends string> {
    id: T;
    label: string;
}

export interface CategoryItem {
    id: string;
    title: string;
    // color: string; TODO: Артём?
    icon: string;
}

export interface AccountItem {
    id: string;
    title: string;
    balance: string;
    color: string;
}
