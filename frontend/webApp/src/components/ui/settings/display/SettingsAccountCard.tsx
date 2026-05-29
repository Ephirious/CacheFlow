import {AccountItem} from "../types.ts";

interface SettingsAccountCardProps {
    account: AccountItem;
    onClick: (account: AccountItem) => void;
}

const SettingsAccountCard = ({account, onClick}: SettingsAccountCardProps) => {
    return (
        <button
            className="flex w-full items-center gap-4 rounded-xl border border-border-subtle bg-surface-base p-5 text-left cursor-pointer transition-all hover:bg-surface-muted active:scale-[0.98]"
            onClick={() => onClick(account)}
            type="button"
        >
            <div className={`h-12 w-12 rounded-xl`} style={{ backgroundColor: account.color }}></div>
            <div className="flex flex-col gap-1">
                <p className="text-base font-semibold text-text-primary">{account.title}</p>
                <p className="text-xl font-bold text-text-primary">{account.balance}</p>
            </div>
        </button>
    );
};

export default SettingsAccountCard;
