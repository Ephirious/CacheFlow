import MainCard from "../ui/main/MainCard.tsx";
import Transactions from "../ui/main/Transactions.tsx";
import {MainComponent, MainState, ManageTransactionState} from "k2ts";
import {useValue, when} from "interop";
import BottomSheet from "../ui/main/BottomSheet.tsx";
import {useState} from "react";

import CreateTransactionButton from "../ui/main/CreateTransactionButton.tsx";
import CreateTransactionContent from "../ui/main/CreateTransactionContent.tsx";


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

const MainOK = ({ component }: { component: MainComponent }) => {
    const summaryState = useValue(component.summaryComponent.state);
    const transactionsState = useValue(component.transactionsComponent.state);
    const manageTransactionState = useValue(component.manageTransactionComponent.state);

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [themeElement, setThemeElement] = useState<HTMLElement>();
    const [sheetPortalEl, setSheetPortalEl] = useState<HTMLDivElement | null>(null);

    return (
        <div
            ref={(el) => el && setThemeElement(el)}
            className="fixed inset-0 pt-[env(safe-area-inset-top)] bg-[#4F39F6]"
        >
            <main className="relative h-full w-full">
                <MainCard data={{
                    accounts: summaryState.accounts.asJsReadonlyArrayView(),
                    balance: summaryState.overallBalance,
                    percentage: summaryState.profitPercentage
                }}/>

                <div ref={setSheetPortalEl} className="pointer-events-none fixed inset-0 z-30" aria-hidden />

                <BottomSheet
                    containerEl={themeElement}
                    portalContainer={sheetPortalEl}
                    themeMode="interpolate"
                    themeEnabled={!isCreateModalOpen}
                >
                    <Transactions transactions={transactionsState.transactions.asJsReadonlyArrayView()}/>
                </BottomSheet>
            </main>

            <CreateTransactionButton onClick={() => setIsCreateModalOpen(true)}/>

            <BottomSheet
                containerEl={themeElement}
                open={isCreateModalOpen}
                onOpenChange={setIsCreateModalOpen}
                snapPoints={[1]}
                initialSnapPoint={1}
                dismissible={true}
                modal={true}
                zIndex={60}
                backgroundColor="#EBEBF0"
                className="sm:hidden"
                themeMode="interpolate"
                themeTargetColor="#EBEBF0"
                useCurrentThemeAsBase={true}
                contentPaddingBottom="calc(env(safe-area-inset-bottom))"
            >
                {when(manageTransactionState)
                    .on(ManageTransactionState.OK, (okState) => (
                        <CreateTransactionContent
                            component={component.manageTransactionComponent}
                            state={okState}
                            close={() => setIsCreateModalOpen(false)}
                        />
                    ))
                    .otherwise(() => <div>error</div>)}
            </BottomSheet>
        </div>
    );
};

export default Main;