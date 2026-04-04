import MainCard from "../ui/main/MainCard.tsx";
import Transactions from "../ui/main/Transactions.tsx";
import {MainComponent, MainState} from "k2ts";
import {useValue, when} from "interop";
import TransactionBottomSheet from "../ui/main/BottomSheet.tsx";
import {useState} from "react";

import CreateTransactionButton from "../ui/main/CreateTransactionButton.tsx";
import CreateTransactionBottomSheet from "../ui/main/CreateTransactionBottomSheet.tsx";


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

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

    const [themeElement, setThemeElement] = useState<HTMLElement>();
    const [sheetPortalEl, setSheetPortalEl] = useState<HTMLDivElement | null>(null);

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
                <div
                    ref={setSheetPortalEl}
                    className="pointer-events-none fixed inset-0 z-30"
                    aria-hidden
                />
                <TransactionBottomSheet containerEl={themeElement} portalContainer={sheetPortalEl}>
                    <Transactions transactions={transactionsState.transactions.asJsReadonlyArrayView()}/>
                </TransactionBottomSheet>
            </main>

            <CreateTransactionButton onClick={() => setIsCreateModalOpen(true)}/>
            <CreateTransactionBottomSheet
                component={component.manageTransactionComponent}
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                containerEl={themeElement}
            />
        </div>
    )
}

export default Main;