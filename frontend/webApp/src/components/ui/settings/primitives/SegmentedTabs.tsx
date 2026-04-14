import {SettingsTabOption} from "../types.ts";

interface SegmentedTabsProps<T extends string> {
    options: SettingsTabOption<T>[];
    active: T;
    onChange: (value: T) => void;
}

const tabClass = "w-full h-10 rounded-xl text-base font-medium";

const SegmentedTabs = <T extends string>({options, active, onChange}: SegmentedTabsProps<T>) => {
    return (
        <div className="rounded-xl bg-surface-muted p-1">
            <div className={`grid gap-1 ${options.length === 2 ? "grid-cols-2" : "grid-cols-1"}`}>
                {options.map((option) => (
                    <button
                        key={option.id}
                        className={`${tabClass} ${active === option.id ? "bg-surface-base text-text-primary shadow-sm" : "text-text-nav"}`}
                        onClick={() => onChange(option.id)}
                        type="button"
                    >
                        {option.label}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default SegmentedTabs;
