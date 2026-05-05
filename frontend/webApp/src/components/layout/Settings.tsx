import {useEffect, useRef} from "react";
import {
    AccountModal,
    AccountsSection,
    CategoryModal,
    CategoriesSection,
    SettingsHeader,
    SettingsMainSection,
    SyncSection, CategoryTypeId
} from "../ui/settings";
import {
    CategoriesPagesComponent,
    SettingsChild,
    SettingsComponent,
    SettingsOutput,
    CategoryType,
    CategoriesPagesOutput,
    AccountsComponent,
    SettingsModalChild,
    SettingsAction,
    setJsTheme,
    SettingsIntent
} from "k2ts";
import {useValue, when} from "interop";
import {useActions} from "../../interop/useActions.ts";

const isStandalone = () =>
    window.matchMedia("(display-mode: standalone)").matches || (window.navigator as Navigator & {
        standalone?: boolean
    }).standalone;

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

const CategoriesPages = ({component}: { component: CategoriesPagesComponent }) => {

    const pages = useValue(component.childPages)
    const activeChild = pages.active;

    const tsCategoryType: CategoryTypeId = activeChild.type === CategoryType.INCOME ? "income" : "outcome"

    const categories = useValue(activeChild.categoriesList);


    return <CategoriesSection
        categories={categories.asJsReadonlyArrayView()}
        categoryType={tsCategoryType}
        onCategoryClick={(category) => {
            component.onItemClick(category.id);
        }}
        onCategoryTypeChange={
            (tsCategory) => {
                component.onOutput(tsCategory === 'income' ? CategoriesPagesOutput.NavigateToIncome : CategoriesPagesOutput.NavigateToOutcome)
            }
        }
    />
}

const Accounts = ({component}: { component: AccountsComponent }) => {
    const accounts = useValue(component.accountsList);

    return <AccountsSection
        accounts={accounts.asJsReadonlyArrayView()}
        onAccountClick={(account) => {
            component.onItemClick(account.id);
        }}
    />
}

const Settings = ({component}: { component: SettingsComponent }) => {

    const state = useValue(component.state)

    useActions(component, (action) => {
        when(action)
            .on(SettingsAction.ThemeChanged, ({theme}) => {
                setJsTheme(theme)
            })
            .run()
    });

    const pages = useValue(component.childPages)
    const activeChild = pages.active;
    const isCategoriesActive = activeChild instanceof SettingsChild.CategoriesChild;

    const tab = isCategoriesActive ? 'categories' : 'accounts'

    const modalSlot = useValue(component.jsModalSlot);
    const modalChild = modalSlot.instance
    const iosTintKickId = useRef<number | null>(null);

    useEffect(() => {
        const themeMeta = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');
        const appleStatusMeta = document.head.querySelector<HTMLMetaElement>('meta[name="apple-mobile-web-app-status-bar-style"]');
        const isIOS = /iPhone|iPad|iPod/.test(window.navigator.userAgent);
        const needsManualKick = isIOS && !isStandalone();
        let tintBarEl: HTMLDivElement | null = null;

        const applyWhiteTheme = () => {
            const bgColor = getComputedStyle(document.documentElement).getPropertyValue("--color-surface-base").trim() || "#ffffff";
            if (themeMeta) themeMeta.content = bgColor;
            if (appleStatusMeta) appleStatusMeta.content = "default";
            document.documentElement.style.backgroundColor = "";
            document.body.style.backgroundColor = "";
        };

        applyWhiteTheme();

        if (needsManualKick) {
            let frame = 0;
            const kickThemeRefresh = () => {
                const bgColor = getComputedStyle(document.documentElement).getPropertyValue("--color-surface-base").trim() || "#ffffff";
                const nextTintBar = createTintBar(bgColor);
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
        <div className="flex pb-6 flex-col bg-surface-base min-h-screen">
            <SettingsHeader curTheme={state.currentTheme}
                            onThemeClick={() => component.intent(SettingsIntent.ChangeTheme)}
                            tab={tab}
                            onTabChange={(newTab) => {
                                if (newTab === "categories") {
                                    component.onOutput(SettingsOutput.NavigateToCategories)
                                } else {
                                    component.onOutput(SettingsOutput.NavigateToAccounts)
                                }
                            }}/>
            <SettingsMainSection
                onAddClick={() => (activeChild.component.onCreateClick())}
                tab={tab}
            >
                {
                    when(activeChild)
                        .on(SettingsChild.CategoriesChild, (child) => (
                            <CategoriesPages component={child.component}/>
                        ))
                        .on(SettingsChild.AccountsChild, (child) => (
                            <Accounts component={child.component}/>
                        ))
                        .run()
                }
            </SettingsMainSection>
            <SyncSection/>

            {
                modalChild && when(modalChild)
                    .on(SettingsModalChild.CreateCategoryChild, (child) => (
                        <CategoryModal
                            component={child.component}
                            mode="add"
                            onClose={() => component.dismissSlot()}
                            open={true}
                        />
                    ))
                    .on(SettingsModalChild.EditCategoryChild, (child) => (
                        <CategoryModal
                            component={child.component}
                            mode="edit"
                            onClose={() => component.dismissSlot()}
                            open={true}
                        />
                    ))
                    .on(SettingsModalChild.CreateAccountChild, (child) => (
                        <AccountModal
                            component={child.component}
                            mode="add"
                            onClose={() => component.dismissSlot()}
                            open={true}/>
                    ))
                    .on(SettingsModalChild.EditAccountChild, (child) => (
                        <AccountModal
                            component={child.component}
                            mode="edit"
                            onClose={() => component.dismissSlot()}
                            open={true}/>
                    ))

                    .run()
            }
        </div>
    );
};

export default Settings;
