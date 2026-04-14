import {useState} from "react";
import {
    AccountsSection,
    CategoriesSection,
    CategoryType,
    SettingsHeader,
    SettingsMainSection,
    SettingsTab,
    SyncSection
} from "../ui/settings";

const Settings = () => {
    const [tab, setTab] = useState<SettingsTab>("categories");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");

    return (
        <div className="flex h-screen w-full flex-col bg-surface-subtle pt-[env(safe-area-inset-top)]">
            <SettingsHeader tab={tab} onTabChange={setTab}/>
            <SettingsMainSection tab={tab}>
                {tab === "categories" ? (
                    <CategoriesSection categoryType={categoryType} onCategoryTypeChange={setCategoryType}/>
                ) : (
                    <AccountsSection/>
                )}
            </SettingsMainSection>
            <SyncSection/>
        </div>
    );
};

export default Settings;
