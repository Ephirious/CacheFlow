import {PiClockClockwise} from "react-icons/pi";
import {accountOptions} from "../data.ts";
import {StatsAccountType} from "../types.ts";

interface AccountSelectorProps {
    account: StatsAccountType;
    onChange: (account: StatsAccountType) => void;
}

const AccountSelector = ({account, onChange}: AccountSelectorProps) => {
    return (
        <div className="flex">
            <div className="flex gap-2 overflow-x-auto no-scrollbar">
                {accountOptions.map((option) => {
                    const isActive = account === option.value;
                    return (
                        <button
                            className={`inline-flex shrink-0 items-center gap-1.5 rounded-xl border px-2.5 py-1.5 text-xs font-medium ${
                                isActive
                                    ? "border-brand-primary/30 bg-brand-primary/10 text-brand-primary"
                                    : "border-border-strong bg-surface-base text-text-secondary"
                            }`}
                            key={option.value}
                            onClick={() => onChange(option.value)}
                            type="button"
                        >
                            <PiClockClockwise className={`h-6 w-6  ${isActive ? "text-brand-primary" : "text-brand-primary/70"}`}/>
                            {option.label}
                        </button>
                    );
                })}
            </div>
        </div>
    );
};

export default AccountSelector;
