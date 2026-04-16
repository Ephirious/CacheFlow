import {useEffect, useRef, useState} from "react";
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

const isStandalone = () =>
    window.matchMedia("(display-mode: standalone)").matches || (window.navigator as Navigator & { standalone?: boolean }).standalone;

const createTintBar = (color: string) => {
    const bar = document.createElement("div");
    Object.assign(bar.style, {
        position: "fixed",
        top: "0",
        left: "0",
        right: "0",
        zIndex: "9999",
        height: "6px",
        backgroundColor: color,
        pointerEvents: "none",
    });
    return bar;
};

const Settings = () => {
    const [tab, setTab] = useState<SettingsTab>("categories");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");
    const [addAccountOpen, setAddAccountOpen] = useState(false);
    const [editAccountOpen, setEditAccountOpen] = useState(false);
    const [addCategoryOpen, setAddCategoryOpen] = useState(false);
    const [editCategoryOpen, setEditCategoryOpen] = useState(false);
    const [selectedAccount, setSelectedAccount] = useState<AccountItem | undefined>(accounts[0]);
    const [selectedCategory, setSelectedCategory] = useState<CategoryItem | undefined>(categoryCards[0]);
    const iosTintKickId = useRef<number | null>(null);

    useEffect(() => {
        const themeMeta = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');
        const appleStatusMeta = document.head.querySelector<HTMLMetaElement>('meta[name="apple-mobile-web-app-status-bar-style"]');
        const isIOS = /iPhone|iPad|iPod/.test(window.navigator.userAgent);
        const needsManualKick = isIOS && !isStandalone();
        let tintBarEl: HTMLDivElement | null = null;

        const applyWhiteTheme = () => {
            if (themeMeta) themeMeta.content = "#ffffff";
            if (appleStatusMeta) appleStatusMeta.content = "default";
            document.documentElement.style.backgroundColor = "#ffffff";
            document.body.style.backgroundColor = "#ffffff";
        };

        applyWhiteTheme();

        if (needsManualKick) {
            let frame = 0;
            const kickThemeRefresh = () => {
                const nextTintBar = createTintBar("#ffffff");
                if (!tintBarEl) {
                    document.body.appendChild(nextTintBar);
                } else {
                    tintBarEl.replaceWith(nextTintBar);
                }
                tintBarEl = nextTintBar;
                applyWhiteTheme();

                if (frame < 45) {
                    frame += 1;
                    iosTintKickId.current = requestAnimationFrame(kickThemeRefresh);
                }
            };

            kickThemeRefresh();
        }

        return () => {
            if (iosTintKickId.current !== null) {
                cancelAnimationFrame(iosTintKickId.current);
            }
            tintBarEl?.remove();
        };
    }, []);

    return (
        <div className="flex pb-6 flex-col bg-surface-subtle min-h-screen">
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
