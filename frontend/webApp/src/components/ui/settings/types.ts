import {ReactNode} from "react";

export type SettingsTab = "categories" | "accounts";
export type CategoryType = "expense" | "income";

export interface SettingsTabOption<T extends string> {
    id: T;
    label: string;
}

export interface CategoryItem {
    title: string;
    color: string;
    icon: ReactNode;
}

export interface AccountItem {
    title: string;
    balance: string;
    color: string;
}
