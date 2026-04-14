import {accounts} from "../data.tsx";
import {SettingsAccountCard} from "../display";

const AccountsSection = () => {
    return (
        <div className="flex flex-col gap-3">
            {accounts.map((account) => (
                <SettingsAccountCard key={account.title} account={account}/>
            ))}
        </div>
    );
};

export default AccountsSection;
