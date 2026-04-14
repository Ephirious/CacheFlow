import {AccountItem} from "../types.ts";

const SettingsAccountCard = ({account}: { account: AccountItem }) => {
    return (
        <button
            className="flex w-full items-center gap-4 rounded-xl border border-border-subtle bg-surface-base p-5 text-left"
            type="button"
        >
            <div className={`h-12 w-12 rounded-xl ${account.color}`}></div>
            <div className="flex flex-col gap-1">
                <p className="text-base font-semibold">{account.title}</p>
                <p className="text-xl font-bold">{account.balance}</p>
            </div>
        </button>
    );
};

export default SettingsAccountCard;
