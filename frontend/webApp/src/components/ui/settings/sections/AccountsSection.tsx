import {Account} from "k2ts";

import {SettingsAccountCard} from "../display";
import {AccountItem} from "../types.ts";

interface AccountsSectionProps {
    accounts: readonly Account[]
    onAccountClick: (account: AccountItem) => void;
}

const AccountsSection = ({accounts, onAccountClick}: AccountsSectionProps) => {
    return (
        <div className="grid grid-cols-1 gap-3 lg:grid-cols-2">
            {
                accounts.map((account) => (
                    <SettingsAccountCard account={
                        {
                            id: account.id,
                            title: account.title,
                            balance: account.balance.prettyString() + " ₽",
                            color: account.color.normalizedHex
                        }
                    } key={account.id} onClick={onAccountClick}/>
                ))
            }
        </div>
    );
};

export default AccountsSection;
