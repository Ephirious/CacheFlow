import {MainCard, Transactions, BottomSheet, CreateTransactionButton, CreateTransaction} from "../ui/main";
import {MainAction, MainComponent, MainState, ManageTransactionComponent, ManageTransactionState} from "k2ts";
import {useValue, when} from "interop";
import {useEffect, useLayoutEffect, useRef, useState} from "react";
import {useActions} from "interop/useActions";


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

// TODO: Артём, отрефактори, пж – вынес, чтобы хук state был сверху
const ManageTransactionContent = ({
                                      component,
                                      onClose
                                  }: {
    component: ManageTransactionComponent,
    onClose: () => void
}) => {
    const state = useValue(component.state);

    return when(state)
        .on(ManageTransactionState.OK, (okState) => (
            <CreateTransaction
                component={component}
                state={okState}
                close={onClose}
            />
        ))
        .otherwise(() => <div>error</div>);
};

const MainOK = ({component}: { component: MainComponent }) => {
    const sheetThemeThreshold = 0.92;

    const summaryState = useValue(component.summaryComponent.state);
    const transactionsState = useValue(component.transactionsComponent.state);

    const manageTransactionSlot = useValue(component.jsManageTransactionSlot);
    const manageTransactionComponent = manageTransactionSlot.instance
    const [isManageOpen, setIsManageOpen] = useState(false);

    // Обработка нажатия "назад" (android, desktop)
    useEffect(() => {
        const handlePopState = (e: PopStateEvent) => {
            if (isManageOpen) {
                setIsManageOpen(false)
            }
        };

        window.addEventListener("popstate", handlePopState);
        return () => window.removeEventListener("popstate", handlePopState);
    }, [isManageOpen, component]);

    useLayoutEffect(() => {
        if (manageTransactionComponent) {
            setIsManageOpen(true);
        }
    }, [manageTransactionComponent]);

    useActions(component, (action) => {
        when(action)
            .is(MainAction.HideManageTransaction, () => {
                setIsManageOpen(false);
            })
            .run()
    });

    // TODO: Артём, отрефактори (спросишь потом, чё это)
    useEffect(() => {
        if (!isManageOpen && manageTransactionComponent) {
            const timer = setTimeout(() => {
                component.setIsManageTransactionOpen(false);
            }, 350);
            return () => clearTimeout(timer);
        }
    }, [isManageOpen, manageTransactionComponent]);

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

                <div ref={setSheetPortalEl} className="pointer-events-none fixed inset-0 z-30" aria-hidden/>

                <BottomSheet
                    key={dynamicSnapPoint}
                    containerEl={themeElement}
                    portalContainer={sheetPortalEl}
                    snapPoints={[dynamicSnapPoint, 1]}
                    initialSnapPoint={dynamicSnapPoint}
                    themeMode="interpolate"
                    themeEnabled={!isManageOpen}
                    themeInterpolationStartThreshold={sheetThemeThreshold}
                >
                    <Transactions transactions={transactionsState.transactions.asJsReadonlyArrayView()}/>
                </BottomSheet>
            </main>

            <CreateTransactionButton onClick={() => component.setIsManageTransactionOpen(true)}/>

            <BottomSheet
                containerEl={themeElement}
                open={isManageOpen}
                onOpenChange={setIsManageOpen}
                onAnimationEnd={() => {component.setIsManageTransactionOpen(false)}}
                snapPoints={[1]}
                initialSnapPoint={1}
                dismissible={true}
                modal={true}
                zIndex={60}
                backgroundColor="var(--color-surface-sheet)"
                className="sm:hidden"
                themeMode="interpolate"
                themeTargetColor="var(--color-surface-sheet)"
                useCurrentThemeAsBase={true}
                themeInterpolationStartThreshold={sheetThemeThreshold}
                contentPaddingBottom="calc(env(safe-area-inset-bottom))"
            >
                {manageTransactionComponent && <ManageTransactionContent component={manageTransactionComponent}
                                                                         onClose={() => setIsManageOpen(false)}/>}
            </BottomSheet>
        </div>
    );
};

export default Main;
