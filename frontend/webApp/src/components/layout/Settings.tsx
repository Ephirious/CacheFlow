import {useState} from "react";
import {
    FiCpu,
    FiDownload,
    FiLogIn,
    FiMoon,
    FiSettings,
    FiSmile,
    FiShoppingCart,
    FiTruck
} from "react-icons/fi";
import {LuShirt, LuUtensils} from "react-icons/lu";

type Tab = "categories" | "accounts";
type CategoryType = "expense" | "income";

const categoryCards = [
    {title: "Продукты", color: "text-category-groceries bg-category-groceries-soft", icon: <FiShoppingCart className="h-6 w-6"/>},
    {title: "Транспорт", color: "text-category-transport bg-category-transport-soft", icon: <FiTruck className="h-6 w-6"/>},
    {title: "Развлечения", color: "text-category-fun bg-category-fun-soft", icon: <FiSmile className="h-6 w-6"/>},
    {title: "Одежда", color: "text-category-clothes bg-category-clothes-soft", icon: <LuShirt className="h-6 w-6"/>},
    {title: "Еда", color: "text-category-food bg-category-food-soft", icon: <LuUtensils className="h-6 w-6"/>},
    {title: "Техника", color: "text-category-tech bg-category-tech-soft", icon: <FiCpu className="h-6 w-6"/>}
];

const accounts = [
    {title: "Счёт1", balance: "100 000 ₽", color: "bg-account-palette-1"},
    {title: "Счёт2", balance: "100 000 ₽", color: "bg-account-palette-2"},
    {title: "Счёт3", balance: "100 000 ₽", color: "bg-account-palette-3"},
    {title: "Счёт4", balance: "100 000 ₽", color: "bg-account-palette-4"},
    {title: "Наличные", balance: "25 000 ₽", color: "bg-account-palette-5"},
    {title: "Сбер", balance: "150 000 ₽", color: "bg-account-palette-6"},
    {title: "Т-банк", balance: "80 000 ₽", color: "bg-account-palette-7"},
    {title: "Крипта", balance: "200 000 ₽", color: "bg-account-palette-8"}
];

const tabClass = "w-full h-10 rounded-xl text-base font-medium";

const Settings = () => {
    const [tab, setTab] = useState<Tab>("categories");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");

    return (
        <div
            className="flex flex-col w-full h-screen bg-surface-subtle"
            style={{
                marginTop: "env(safe-area-inset-top"
            }}
        >
            <div className="flex flex-col gap-6 p-6 pt-3 bg-surface-base border-b border-border-subtle">
                <div className="flex items-center justify-between gap-6">
                    <div className="flex items-center gap-3">
                        <div className="rounded-2xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                            <FiSettings className="h-6 w-6"/>
                        </div>
                        <h1 className="text-2xl font-bold">Настройки</h1>
                    </div>
                    <button className="rounded-2xl p-2 bg-surface-muted" type="button">
                        <FiMoon className="h-6 w-6"/>
                    </button>
                </div>

                <div className="rounded-xl bg-surface-muted p-1">
                    <div className="flex">
                        <button
                            className={
                                `${tabClass} 
                        ${tab === "categories" ? "bg-surface-base text-text-primary shadow-sm" : "text-text-nav"}`}
                            onClick={() => setTab("categories")}
                            type="button"
                        >
                            Категории
                        </button>
                        <button
                            className={`${tabClass} ${tab === "accounts" ? "bg-surface-base text-text-primary shadow-sm" : "text-text-nav"}`}
                            onClick={() => setTab("accounts")}
                            type="button"
                        >
                            Счета
                        </button>
                    </div>
                </div>
            </div>
            <div className="flex flex-col p-6 gap-4">
                <div className="flex items-center justify-between">
                    <h2 className="text-lg font-bold">{tab === "categories" ? "Категории" : "Счета"}</h2>
                    <button
                        className="rounded-xl bg-brand-primary px-4 py-2 text-base font-medium text-brand-on-primary"
                        type="button"
                    >
                        + Добавить
                    </button>
                </div>

                {tab === "categories" ? (
                    <div className="flex flex-col gap-4">
                        <div className="rounded-xl bg-surface-muted p-1">
                            <div className="grid grid-cols-2 gap-1">
                                <button
                                    className={`${tabClass} ${categoryType === "expense" ? "bg-surface-base text-text-primary shadow-sm" : "text-text-nav"}`}
                                    onClick={() => setCategoryType("expense")}
                                    type="button"
                                >
                                    Расходы
                                </button>
                                <button
                                    className={`${tabClass} ${categoryType === "income" ? "bg-surface-base text-text-primary shadow-sm" : "text-text-nav"}`}
                                    onClick={() => setCategoryType("income")}
                                    type="button"
                                >
                                    Доходы
                                </button>
                            </div>
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                            {categoryCards.map((item) => (
                                <button
                                    key={item.title}
                                    className="flex flex-col rounded-xl border items-center border-border-strong bg-surface-base py-2 gap-2"
                                    type="button"
                                >
                                    <div className={`flex rounded-xl p-3 ${item.color}`}>
                                        {item.icon}
                                    </div>
                                    <p className="text-sm font-semibold">{item.title}</p>
                                </button>
                            ))}
                        </div>
                    </div>
                ) : (
                    <div className="flex flex-col gap-3">
                        {accounts.map((account) => (
                            <button
                                key={account.title}
                                className="flex w-full items-center gap-4 rounded-xl border border-border-subtle bg-surface-base p-5 text-left"
                                type="button"
                            >
                                <div className={`h-12 w-12 rounded-xl ${account.color}`}></div>
                                <div className="flex flex-col gap-1">
                                    <p className="text-base font-semibold">{account.title}</p>
                                    <p className="text-xl font-bold">{account.balance}</p>
                                </div>
                            </button>
                        ))}
                    </div>
                )}
            </div>

            <div className="flex flex-col rounded-xl border border-border-default p-6 mx-6 gap-4">
                <h2 className="text-lg font-semibold text-text-primary">Синхронизация</h2>
                <div className="flex flex-col gap-3">
                    <button
                        className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                        type="button">
                                <span className="rounded-xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                                    <FiLogIn className="h-5 w-5"/>
                                </span>
                        Вход/Выход
                    </button>
                    <button
                        className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                        type="button">
                                <span className="rounded-full bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                                    <FiDownload className="h-5 w-5"/>
                                </span>
                        Экспорт в CSV
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Settings;
