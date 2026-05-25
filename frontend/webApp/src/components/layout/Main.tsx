import {MainCard, Transactions, BottomSheet, CreateTransactionButton, CreateTransaction} from "../ui/main";
import {
    MainAction,
    MainComponent,
    MainState,
    ManageTransactionComponent,
    ManageTransactionState,
    TransactionsIntent
} from "k2ts";
import {useValue, when} from "interop";
import {useEffect, useLayoutEffect, useRef, useState} from "react";
import {createPortal} from "react-dom";
import {useActions} from "interop/useActions";
import {FiX} from "react-icons/fi";


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

    useEffect(() => {
        if (state instanceof ManageTransactionState.FatalError) {
            console.error("[ManageTransaction] FatalError", {
                message: state.message,
                lastValidForm: state.lastValidForm
            });
            return;
        }
        console.debug("[ManageTransaction] State changed", state);
    }, [state]);

    return when(state)
        .on(ManageTransactionState.OK, (okState) => (
            <CreateTransaction
                component={component}
                state={okState}
                close={onClose}
            />
        ))
        .on(ManageTransactionState.FatalError, (errorState) => (
            <div className="px-6 py-4 text-state-danger">Ошибка: {errorState.message}</div>
        ))
        .otherwise(() => {
            console.error("[ManageTransaction] Unknown state", state);
            return <div className="px-6 py-4 text-state-danger">error</div>;
        });
};

const MainOK = ({component}: { component: MainComponent }) => {
    const sheetThemeThreshold = 0.92;
    const desktopMediaQuery = "(min-width: 1024px)";
    const modalMediaQuery = "(min-width: 640px)";

    const summaryState = useValue(component.summaryComponent.state);
    const transactionsState = useValue(component.transactionsComponent.state);

    const manageTransactionSlot = useValue(component.jsManageTransactionSlot);
    const manageTransactionComponent = manageTransactionSlot.instance
    const [isManageOpen, setIsManageOpen] = useState(false);
    const [isDesktop, setIsDesktop] = useState(() => window.matchMedia(desktopMediaQuery).matches);
    const [useDesktopModal, setUseDesktopModal] = useState(() => window.matchMedia(modalMediaQuery).matches);

    useEffect(() => {
        const desktopMql = window.matchMedia(desktopMediaQuery);
        const modalMql = window.matchMedia(modalMediaQuery);

        const handleDesktopChange = (event: MediaQueryListEvent) => setIsDesktop(event.matches);
        const handleModalChange = (event: MediaQueryListEvent) => setUseDesktopModal(event.matches);

        setIsDesktop(desktopMql.matches);
        setUseDesktopModal(modalMql.matches);
        desktopMql.addEventListener("change", handleDesktopChange);
        modalMql.addEventListener("change", handleModalChange);

        return () => {
            desktopMql.removeEventListener("change", handleDesktopChange);
            modalMql.removeEventListener("change", handleModalChange);
        };
    }, []);

    // Обработка нажатия "назад" (android, desktop)
    useEffect(() => {
        const handlePopState = () => {
            if (isManageOpen) {
                setIsManageOpen(false)
            }
        };

        window.addEventListener("popstate", handlePopState);
        return () => window.removeEventListener("popstate", handlePopState);
    }, [isManageOpen, component]);

    useEffect(() => {
        if (!useDesktopModal || !isManageOpen) {
            return;
        }

        const onKeyDown = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                setIsManageOpen(false);
            }
        };

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [isManageOpen, useDesktopModal]);

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
        if (isDesktop) {
            return;
        }

        if (cardRef.current) {
            const cardHeight = cardRef.current.offsetHeight;
            const screenHeight = window.innerHeight;

            const calculatedPoint = (screenHeight - cardHeight) / screenHeight;
            const roundedPoint = Math.round(calculatedPoint * 100) / 100;
            setDynamicSnapPoint(roundedPoint);
        }
    }, [isDesktop, summaryState]);

    return (
        <div
            ref={(el) => el && setThemeElement(el)}
            className="fixed inset-0 bg-brand-primary pt-[env(safe-area-inset-top)] lg:relative lg:min-h-screen lg:bg-surface-base lg:pt-0"
        >
            <main className="relative h-full w-full lg:mx-auto lg:flex lg:min-h-screen lg:max-w-6xl lg:flex-col lg:px-6 lg:py-6">
                <div ref={cardRef}>
                    <MainCard data={{
                        accounts: summaryState.accounts.asJsReadonlyArrayView(),
                        balance: summaryState.overallBalance,
                        percentage: summaryState.profitPercentage
                    }}/>
                </div>

                {isDesktop ? (
                    <section className="mt-4 rounded-3xl border border-border-subtle bg-surface-sheet shadow-sm">
                        <Transactions
                            transactions={transactionsState.transactions.asJsReadonlyArrayView()}
                            onEditClick={(transactionId) => {
                                component.openTransactionToEdit(transactionId);
                            }}
                            onLoadMore={() => component.transactionsComponent.intent(TransactionsIntent.LoadMore)}
                        />
                    </section>
                ) : (
                    <>
                        <div ref={setSheetPortalEl} className="pointer-events-none fixed inset-0 z-30" aria-hidden/>

                        <BottomSheet
                            key={dynamicSnapPoint}
                            containerEl={themeElement}
                            portalContainer={sheetPortalEl}
                            snapPoints={[dynamicSnapPoint, 1]}
                            initialSnapPoint={dynamicSnapPoint}
                            repositionInputs={false}
                            themeMode="interpolate"
                            themeEnabled={!isManageOpen}
                            themeInterpolationStartThreshold={sheetThemeThreshold}
                        >
                            <Transactions
                                transactions={transactionsState.transactions.asJsReadonlyArrayView()}
                                onEditClick={(transactionId) => {
                                    component.openTransactionToEdit(transactionId);
                                }}
                            />
                        </BottomSheet>
                    </>
                )}
            </main>

            <CreateTransactionButton
                className="bottom-32 right-5 sm:bottom-8 sm:right-8"
                onClick={() => component.setIsManageTransactionOpen(true)}
            />

            {!useDesktopModal && (
                <BottomSheet
                    containerEl={themeElement}
                    open={isManageOpen}
                    onOpenChange={setIsManageOpen}
                    onAnimationEnd={(isOpen) => {
                        if (!isOpen) {
                            component.setIsManageTransactionOpen(false);
                        }
                    }}
                    snapPoints={[1]}
                    initialSnapPoint={1}
                    dismissible={true}
                    modal={true}
                    repositionInputs={false}
                    fixed={true}
                    zIndex={60}
                    backgroundColor="var(--color-surface-base)"
                    className="sm:hidden"
                    themeMode="interpolate"
                    themeTargetColor="var(--color-surface-base)"
                    useCurrentThemeAsBase={true}
                    themeInterpolationStartThreshold={sheetThemeThreshold}
                    contentPaddingBottom="calc(env(safe-area-inset-bottom))"
                >
                    {manageTransactionComponent && <ManageTransactionContent component={manageTransactionComponent}
                                                                             onClose={() => setIsManageOpen(false)}/>}
                </BottomSheet>
            )}

            {useDesktopModal && isManageOpen && manageTransactionComponent && createPortal(
                <div
                    className="fixed inset-0 z-[100] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[2px]"
                    onClick={() => setIsManageOpen(false)}
                >
                    <div
                        className="flex max-h-[calc(100vh-4rem)] w-full max-w-2xl min-h-0 flex-col rounded-3xl bg-surface-sheet shadow-2xl"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="flex justify-end px-6 pb-0 pt-5">
                            <button
                                aria-label="Закрыть"
                                className="rounded-xl p-2 text-text-secondary transition-colors hover:bg-surface-muted hover:text-text-primary"
                                onClick={() => setIsManageOpen(false)}
                                type="button"
                            >
                                <FiX className="h-5 w-5"/>
                            </button>
                        </div>
                        <div className="min-h-0 flex-1 overflow-y-auto px-1 pb-4">
                            <ManageTransactionContent
                                component={manageTransactionComponent}
                                onClose={() => setIsManageOpen(false)}
                            />
                        </div>
                    </div>
                </div>,
                document.body
            )}
        </div>
    );
};

export default Main;
