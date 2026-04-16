import { MainCard, Transactions, BottomSheet, CreateTransactionButton, CreateTransaction } from "../ui/main";
import {MainComponent, MainState, ManageTransactionState} from "k2ts";
import {useValue, when} from "interop";
import {useLayoutEffect, useRef, useState} from "react";


const Main = ({component}: { component: MainComponent }) => {

    const state = useValue(component.state)

    return (
        when(state)
            .on(MainState.Error, (error) => (
                <div>{error.message}</div>
            ))
            .is(MainState.OK, () =>
                <MainOK component={component}/>
            )
            .run()
    )
}

const MainOK = ({ component }: { component: MainComponent }) => {
    const sheetThemeThreshold = 0.92;

    const summaryState = useValue(component.summaryComponent.state);
    const transactionsState = useValue(component.transactionsComponent.state);
    const manageTransactionState = useValue(component.manageTransactionComponent.state);

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [themeElement, setThemeElement] = useState<HTMLElement>();
    const [sheetPortalEl, setSheetPortalEl] = useState<HTMLDivElement | null>(null);

    const cardRef = useRef<HTMLDivElement>(null);
    const [dynamicSnapPoint, setDynamicSnapPoint] = useState<number>(0.6);

    useLayoutEffect(() => {
        if (cardRef.current) {
            const cardHeight = cardRef.current.offsetHeight;
            const screenHeight = window.innerHeight;

            const calculatedPoint = (screenHeight - cardHeight) / screenHeight;
            const roundedPoint = Math.round(calculatedPoint * 100) / 100;
            setDynamicSnapPoint(roundedPoint);
        }
    }, [summaryState]);

    return (
        <div
            ref={(el) => el && setThemeElement(el)}
            className="fixed inset-0 pt-[env(safe-area-inset-top)] bg-brand-primary"
        >
            <main className="relative h-full w-full">
                <div ref={cardRef}>
                    <MainCard data={{
                        accounts: summaryState.accounts.asJsReadonlyArrayView(),
                        balance: summaryState.overallBalance,
                        percentage: summaryState.profitPercentage
                    }}/>
                </div>

                <div ref={setSheetPortalEl} className="pointer-events-none fixed inset-0 z-30" aria-hidden />

                <BottomSheet
                    key={dynamicSnapPoint}
                    containerEl={themeElement}
                    portalContainer={sheetPortalEl}
                    snapPoints={[dynamicSnapPoint, 1]}
                    initialSnapPoint={dynamicSnapPoint}
                    repositionInputs={false}
                    themeMode="interpolate"
                    themeEnabled={!isCreateModalOpen}
                    themeInterpolationStartThreshold={sheetThemeThreshold}
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
                repositionInputs={false}
                fixed={true}
                zIndex={60}
                backgroundColor="var(--color-surface-sheet)"
                className="sm:hidden"
                themeMode="interpolate"
                themeTargetColor="var(--color-surface-sheet)"
                useCurrentThemeAsBase={true}
                themeInterpolationStartThreshold={sheetThemeThreshold}
                contentPaddingBottom="calc(env(safe-area-inset-bottom))"
            >
                {when(manageTransactionState)
                    .on(ManageTransactionState.OK, (okState) => (
                        <CreateTransaction
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
