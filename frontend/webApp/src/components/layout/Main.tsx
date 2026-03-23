import MainCard from "../ui/MainCard.tsx";
import Transactions from "../ui/Transactions.tsx";


const ACCOUNTS = [
    { id: 1, title: "Наличные", balance: "25 000 ₽", color: "bg-account-orange" },
    { id: 2, title: "Сбербанк", balance: "1 200 000 ₽", color: "bg-green-500" },
    { id: 3, title: "Т-Банк", balance: "619 400 ₽", color: "bg-yellow-400" },
    { id: 4, title: "Инвестиции", balance: "0 ₽", color: "bg-blue-400" },
];

const Main = () => {
    return (
        <main className="w-full h-full">
            <MainCard accounts={ACCOUNTS}/>
            <Transactions/>
        </main>
    )
}

export default Main;