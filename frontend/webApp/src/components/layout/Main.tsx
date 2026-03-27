import MainCard from "../ui/MainCard.tsx";
import Transactions from "../ui/Transactions.tsx";
import {MainComponent, MainState} from "k2ts";
import {useValue, when} from "interop";
import BottomSheet from "../ui/BottomSheet.tsx";
import {Account} from "../../types/types.ts";
import {useState} from "react";

const TEST_ACCOUNTS: Account[] = [
    {title: "Т-Банк", balance: "100 000", color: "bg-orange-500"},
    {title: "Газпромбанк", balance: "100 000", color: "bg-green-500"},
    {title: "Сбер", balance: "100 000", color: "bg-blue-500"},
    {title: "Крипта", balance: "1 000 000", color: "bg-red-500"}
]


const Main = ({component}: { component: MainComponent }) => {

    const state = useValue(component.state)

    return (
        when(state)
            .on(MainState.Error, (error) => {
                {
                    error.message
                }
            })
            .is(MainState.OK, () =>
                <MainOK component={component}/>
            )
            .run()
    )
}

const MainOK = ({component}: { component: MainComponent }) => {
    const summaryState = useValue(component.summaryComponent.state)
    const transactionsState = useValue(component.transactionsComponent.state)

    const [themeElement, setThemeElement] = useState<HTMLElement>();

    return (
        <div ref={(el) => el && setThemeElement(el)}
             className={"fixed h-screen w-screen pt-[env(safe-area-inset-top)]"}
             style={{backgroundColor: "#4F39F6"}}
        >
            <main

                className={"fixed h-screen w-screen"}
                style={{backgroundColor: "#4F39F6"}}
            >
                <MainCard data={{
                    accounts: TEST_ACCOUNTS,
                    balance: summaryState.overallBalance,
                    percentage: summaryState.profitPercentage
                }}/>
                <BottomSheet containerEl={themeElement}>
                    <Transactions transactions={transactionsState.transactions.asJsReadonlyArrayView()}/>
                </BottomSheet>
            </main>
        </div>
    )
}

export default Main;