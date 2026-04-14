import {LuWallet} from "react-icons/lu";
import { AccountCard } from "./index";
import {BigDecimal, Account} from "k2ts";

interface MainCardData {
    accounts: readonly Account[],
    balance: BigDecimal,
    percentage: BigDecimal
}

const MainCard = ({data}: { data: MainCardData }) => {
    const displayBalance = data.balance.prettyString();
    const displayPercentage = data.percentage.toString();
    const accountsList = data.accounts;
    return (
        <div className="p-0 sm:p-6 w-full">
            <div className="
                flex flex-col bg-brand-primary gap-6
                w-full p-6
                sm:rounded-3xl sm:p-10
            ">
                <div className="flex gap-3 items-center">
                    <div className="p-2.5 bg-brand-on-primary/20 rounded-2xl">
                        <LuWallet className="w-7 h-7 stroke-brand-on-primary"/>
                    </div>
                    <h1 className="text-2xl sm:text-3xl text-brand-on-primary font-bold">
                        <span className="sm:hidden">CacheFlow</span>
                        <span className="hidden sm:inline text-3xl">Добро пожаловать!</span>
                    </h1>
                </div>

                <div className="flex flex-col gap-1">
                    <div className="text-sm text-brand-on-primary/70 font-medium">Общий баланс</div>
                    <div className="text-5xl sm:text-6xl font-bold text-brand-on-primary">
                        {displayBalance.toString()} ₽
                    </div>
                    <div className="text-sm text-brand-on-primary/70 font-medium">+{displayPercentage.toString()}% за месяц</div>
                </div>
                <div
                    className="
                        grid gap-3 mt-2
                        grid-cols-2
                        sm:grid-cols-4
                    "
                >
                    {accountsList.map((acc, index) => (
                        <AccountCard key={acc.id} account={acc} index={index}/>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default MainCard;
