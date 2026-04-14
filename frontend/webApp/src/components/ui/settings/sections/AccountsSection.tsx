import {accounts} from "../data.tsx";
import {SettingsAccountCard} from "../display";
import {AccountItem} from "../types.ts";

interface AccountsSectionProps {
    onAccountClick: (account: AccountItem) => void;
}

const AccountsSection = ({onAccountClick}: AccountsSectionProps) => {
    return (
        <div className="flex flex-col gap-3">
            {accounts.map((account) => (
                <SettingsAccountCard account={account} key={account.id} onClick={onAccountClick}/>
            ))}
        </div>
    );
};

export default AccountsSection;
