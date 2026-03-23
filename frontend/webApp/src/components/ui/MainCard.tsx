import {LuWallet} from "react-icons/lu";
import AccountCard from "./AccountCard.tsx";



const MainCard = ({ accounts }: { accounts: any[] }) => {
    return (
        <div className="p-0 sm:p-6 w-full">
            <div className="
                flex flex-col bg-brand-indigo gap-4
                w-full p-6
                sm:rounded-3xl sm:p-10
            ">
                <div className="flex gap-3 items-center">
                    <div className="p-2.5 bg-white/20 rounded-2xl">
                        <LuWallet className="w-7 h-7 stroke-white" />
                    </div>
                    <h1 className="text-2xl sm:text-3xl text-white font-bold">
                        <span className="sm:hidden">CashFlow</span>
                        <span className="hidden sm:inline text-3xl">Добро пожаловать!</span>
                    </h1>
                </div>

                <div className="flex flex-col gap-1">
                    <div className="text-sm text-white/70 font-medium">Общий баланс</div>
                    <div className="text-5xl sm:text-6xl font-bold text-white">
                        1 844 400 ₽
                    </div>
                    <div className="text-sm text-white/70 font-medium">+12.5% за месяц</div>
                </div>
                <div
                    className="
                        grid gap-3 mt-2
                        grid-cols-2
                        sm:grid-cols-4
                    "
                >
                    {accounts.map((acc, index) => (
                        <AccountCard key={acc.id} account={acc} index={index}/>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default MainCard;