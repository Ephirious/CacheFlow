import {useState} from "react";
import {
    AccountItem,
    AccountModal,
    AccountsSection,
    CategoryItem,
    CategoryModal,
    CategoriesSection,
    CategoryType,
    SettingsHeader,
    SettingsMainSection,
    SettingsTab,
    SyncSection
} from "../ui/settings";
import {accounts, categoryCards} from "../ui/settings/data.tsx";

const Settings = () => {
    const [tab, setTab] = useState<SettingsTab>("categories");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");
    const [addAccountOpen, setAddAccountOpen] = useState(false);
    const [editAccountOpen, setEditAccountOpen] = useState(false);
    const [addCategoryOpen, setAddCategoryOpen] = useState(false);
    const [editCategoryOpen, setEditCategoryOpen] = useState(false);
    const [selectedAccount, setSelectedAccount] = useState<AccountItem | undefined>(accounts[0]);
    const [selectedCategory, setSelectedCategory] = useState<CategoryItem | undefined>(categoryCards[0]);

    return (
        <div className="flex pb-6 flex-col bg-surface-subtle">
            <SettingsHeader tab={tab} onTabChange={setTab}/>
            <SettingsMainSection
                onAddClick={() => (tab === "categories" ? setAddCategoryOpen(true) : setAddAccountOpen(true))}
                tab={tab}
            >
                {tab === "categories" ? (
                    <CategoriesSection
                        categoryType={categoryType}
                        onCategoryClick={(category) => {
                            setSelectedCategory(category);
                            setEditCategoryOpen(true);
                        }}
                        onCategoryTypeChange={setCategoryType}
                    />
                ) : (
                    <AccountsSection
                        onAccountClick={(account) => {
                            setSelectedAccount(account);
                            setEditAccountOpen(true);
                        }}
                    />
                )}
            </SettingsMainSection>
            <SyncSection/>

            <AccountModal mode="add" onClose={() => setAddAccountOpen(false)} open={addAccountOpen}/>
            <AccountModal
                account={selectedAccount}
                mode="edit"
                onClose={() => setEditAccountOpen(false)}
                open={editAccountOpen}
            />
            <CategoryModal mode="add" onClose={() => setAddCategoryOpen(false)} open={addCategoryOpen}/>
            <CategoryModal
                category={selectedCategory}
                mode="edit"
                onClose={() => setEditCategoryOpen(false)}
                open={editCategoryOpen}
            />
        </div>
    );
};

export default Settings;
