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
    {title: "Продукты", color: "text-rose-500 bg-rose-100", icon: <FiShoppingCart className="h-6 w-6"/>},
    {title: "Транспорт", color: "text-amber-500 bg-amber-100", icon: <FiTruck className="h-6 w-6"/>},
    {title: "Развлечения", color: "text-violet-500 bg-violet-100", icon: <FiSmile className="h-6 w-6"/>},
    {title: "Одежда", color: "text-emerald-500 bg-emerald-100", icon: <LuShirt className="h-6 w-6"/>},
    {title: "Еда", color: "text-teal-500 bg-teal-100", icon: <LuUtensils className="h-6 w-6"/>},
    {title: "Техника", color: "text-pink-500 bg-pink-100", icon: <FiCpu className="h-6 w-6"/>}
];

const accounts = [
    {title: "Счёт1", balance: "100 000 ₽", color: "bg-blue-500"},
    {title: "Счёт2", balance: "100 000 ₽", color: "bg-violet-500"},
    {title: "Счёт3", balance: "100 000 ₽", color: "bg-pink-500"},
    {title: "Счёт4", balance: "100 000 ₽", color: "bg-emerald-500"},
    {title: "Наличные", balance: "25 000 ₽", color: "bg-amber-500"},
    {title: "Сбер", balance: "150 000 ₽", color: "bg-green-500"},
    {title: "Т-банк", balance: "80 000 ₽", color: "bg-yellow-500"},
    {title: "Крипта", balance: "200 000 ₽", color: "bg-indigo-500"}
];

const tabClass = "w-full h-10 rounded-xl text-base font-medium";

const Settings = () => {
    const [tab, setTab] = useState<Tab>("categories");
    const [categoryType, setCategoryType] = useState<CategoryType>("expense");

    return (
        <div
            className="flex flex-col w-full h-screen bg-settings"
            style={{
                marginTop: "env(safe-area-inset-top"
            }}
        >
            <div className="flex flex-col gap-6 p-6 pt-3 bg-white border-b border-settings-border">
                <div className="flex items-center justify-between gap-6">
                    <div className="flex items-center gap-3">
                        <div className="rounded-2xl bg-brand-indigo-l/10 p-2 text-brand-indigo">
                            <FiSettings className="h-6 w-6"/>
                        </div>
                        <h1 className="text-2xl font-bold">Настройки</h1>
                    </div>
                    <button className="rounded-2xl p-2 bg-lt-gray" type="button">
                        <FiMoon className="h-6 w-6"/>
                    </button>
                </div>

                <div className="rounded-xl bg-lt-gray p-1">
                    <div className="flex">
                        <button
                            className={
                                `${tabClass} 
                        ${tab === "categories" ? "bg-white text-slate-900 shadow-sm" : "text-slate-500"}`}
                            onClick={() => setTab("categories")}
                            type="button"
                        >
                            Категории
                        </button>
                        <button
                            className={`${tabClass} ${tab === "accounts" ? "bg-white text-slate-900 shadow-sm" : "text-slate-500"}`}
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
                        className="rounded-xl bg-brand-indigo px-4 py-2 text-base font-medium text-white"
                        type="button"
                    >
                        + Добавить
                    </button>
                </div>

                {tab === "categories" ? (
                    <div className="flex flex-col gap-4">
                        <div className="rounded-xl bg-lt-gray p-1">
                            <div className="grid grid-cols-2 gap-1">
                                <button
                                    className={`${tabClass} ${categoryType === "expense" ? "bg-white text-slate-900 shadow-sm" : "text-slate-500"}`}
                                    onClick={() => setCategoryType("expense")}
                                    type="button"
                                >
                                    Расходы
                                </button>
                                <button
                                    className={`${tabClass} ${categoryType === "income" ? "bg-white text-slate-900 shadow-sm" : "text-slate-500"}`}
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
                                    className="flex flex-col rounded-xl border items-center border-slate-200 bg-white py-2 gap-2"
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
                                className="flex w-full items-center gap-4 rounded-xl border border-settings-border bg-white p-5 text-left"
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

            <div className="flex flex-col rounded-xl border border-settings-border-sync p-6 mx-6 gap-4">
                <h2 className="text-lg font-semibold text-slate-900">Синхронизация</h2>
                <div className="flex flex-col gap-3">
                    <button
                        className="flex w-full items-center gap-2 rounded-lg border border-settings-border bg-white px-3 py-3 text-base"
                        type="button">
                                <span className="rounded-xl bg-brand-indigo-l/10 p-2 text-brand-indigo">
                                    <FiLogIn className="h-5 w-5"/>
                                </span>
                        Вход/Выход
                    </button>
                    <button
                        className="flex w-full items-center gap-2 rounded-lg border border-settings-border bg-white px-3 py-3 text-base"
                        type="button">
                                <span className="rounded-full bg-brand-indigo-l/10 p-2 text-brand-indigo">
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
