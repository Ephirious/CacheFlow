import MainCard from "../ui/MainCard.tsx";
import Transactions from "../ui/Transactions.tsx";
import {MainComponent} from "k2ts";
import {useValue} from "interop";
import {Transaction} from "../../types/types.ts";
import BottomSheet from "../ui/BottomSheet.tsx";
import {Account} from "../../types/types.ts";


const TEST_TRANSACTIONS: Transaction[] = [
    {
        id: 1,
        type: 'Income',
        value: 1000000,
        title: 'Зарплата',
        date: '14.03.2026',
        accountName: 'Счёт1',
        category: {id: 101, name: 'Работа'}
    },
    {
        id: 2,
        type: 'Outcome',
        value: 600,
        title: 'Развлечения',
        date: '14.03.2026',
        accountName: 'Счёт1',
        category: {id: 202, name: 'Кино'}
    },
    {
        id: 3,
        type: 'Transfer',
        value: 5000,
        title: 'Перевод себе',
        date: '13.03.2026',
        accountName: 'Тинькофф',
        from: 'Тинькофф',
        to: 'Сбер'
    },
    {
        id: 4,
        type: 'Outcome',
        value: 1200,
        title: 'Ужин в ресторане',
        date: '12.03.2026',
        accountName: 'Счёт1',
        category: {id: 203, name: 'Еда'}
    },
    {
        id: 5,
        type: 'Income',
        value: 15000,
        title: 'Продажа на Авито',
        date: '10.03.2026',
        accountName: 'Счёт2',
        category: {id: 105, name: 'Прочее'}
    }
]

const TEST_ACCOUNTS: Account[] = [
    {title: "Т-Банк", balance: "100 000", color: "bg-orange-500"},
    {title: "Газпромбанк", balance: "100 000", color: "bg-green-500"},
    {title: "Сбер", balance: "100 000", color: "bg-blue-500"},
    {title: "Крипта", balance: "1 000 000", color: "bg-red-500"}
]


const Main = ({component}: { component: MainComponent }) => {
    const summaryState = useValue(component.summaryComponent.state)
    const transactionsState = useValue(component.transactionsComponent.state)


    return (
        <main className="w-full h-screen bg-brand-indigo">
            <MainCard data={{
                accounts: TEST_ACCOUNTS,
                balance: summaryState.overallBalance,
                percentage: summaryState.profitPercentage
            }}/>
            <BottomSheet>
                <Transactions transactions={TEST_TRANSACTIONS}/>
            </BottomSheet>
        </main>
    )
}

export default Main;