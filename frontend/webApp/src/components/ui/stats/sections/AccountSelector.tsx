import {StatsAccountOption, StatsAccountType} from "../types.ts";

interface AccountSelectorProps {
    account: StatsAccountType;
    options: ReadonlyArray<StatsAccountOption>;
    onChange: (account: StatsAccountType) => void;
}

const AccountSelector = ({account, options, onChange}: AccountSelectorProps) => {
    return (
        <div
            className="flex gap-2 overflow-x-auto pb-1 [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
            style={{scrollbarWidth: "none", msOverflowStyle: "none"}}
        >
            {options.map((option) => {
                const isActive = account === option.value;
                return (
                    <button
                        className={`inline-flex shrink-0 items-center gap-2 rounded-xl border px-3 py-2 text-sm cursor-pointer transition-colors hover:bg-surface-hover active:scale-95 ${
                            isActive
                                ? "border-brand-primary/30 bg-brand-primary/10 text-brand-primary"
                                : "border-border-strong bg-surface-base text-text-secondary"
                        }`}
                        key={option.value}
                        onClick={() => onChange(option.value)}
                        type="button"
                    >
                        <span className="font-medium">{option.label}</span>
                        {option.balance && <span className="text-xs text-text-secondary">{option.balance}</span>}
                    </button>
                );
            })}
        </div>
    );
};

export default AccountSelector;
