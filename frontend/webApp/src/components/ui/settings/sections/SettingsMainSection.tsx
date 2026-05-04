import {ReactNode} from "react";
import {SettingsTab} from "../types.ts";

interface SettingsMainSectionProps {
    tab: SettingsTab;
    children: ReactNode;
    onAddClick: () => void;
}

const SettingsMainSection = ({tab, children, onAddClick}: SettingsMainSectionProps) => {
    return (
        <div className="flex flex-col gap-4 p-6">
            <div className="flex items-center justify-between">
                <h2 className="text-lg font-bold text-text-primary">{tab === "categories" ? "Категории" : "Счета"}</h2>
                <button
                    className="rounded-xl bg-brand-primary px-4 py-2 text-base font-medium text-brand-on-primary"
                    onClick={onAddClick}
                    type="button"
                >
                    + Добавить
                </button>
            </div>
            {children}
        </div>
    );
};

export default SettingsMainSection;
