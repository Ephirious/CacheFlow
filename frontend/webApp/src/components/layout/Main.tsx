import MainCard from "../ui/MainCard.tsx";
import Transactions from "../ui/Transactions.tsx";
import {MainComponent, MainState} from "k2ts";
import {useValue, when} from "interop";
import BottomSheet from "../ui/BottomSheet.tsx";
import {useState} from "react";


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
                    accounts: summaryState.accounts.asJsReadonlyArrayView(),
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