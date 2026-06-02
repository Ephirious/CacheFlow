import {MainCard, Transactions, BottomSheet, CreateTransactionButton, CreateTransaction, Filters} from "../ui/main";
import {
    MainAction,
    MainComponent,
    MainState,
    ManageTransactionComponent,
    ManageTransactionState,
    ManageTransactionModalChild,
    TransactionsIntent,
    TransactionsAction
} from "k2ts";
import {useValue, when} from "interop";
import {useEffect, useLayoutEffect, useRef, useState} from "react";
import {createPortal} from "react-dom";
import {useActions} from "interop/useActions";
import {FiX} from "react-icons/fi";
import { AccountModal, CategoryModal } from "../ui/settings/forms";



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

const ManageTransactionContent = ({
                                       component,
                                       onClose
                                   }: {
    component: ManageTransactionComponent,
    onClose: () => void
}) => {
    const state = useValue(component.state);
    const modalSlot = useValue(component.jsModalSlot);
    const modalInstance = modalSlot.instance;

    return (
        <>
            {when(state)
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
                })
            }
            
            {modalInstance && when(modalInstance)
                .on(ManageTransactionModalChild.CreateCategoryChild, (child) => (
                    <CategoryModal
                        component={child.component}
                        open={true}
                        mode="add"
                        onClose={() => component.dismissSlot()}
                    />
                ))
                .on(ManageTransactionModalChild.CreateAccountChild, (child) => (
                    <AccountModal
                        component={child.component}
                        open={true}
                        mode="add"
                        onClose={() => component.dismissSlot()}
                    />
                ))
                .run()
            }
        </>
    );
};

const MainOK = ({component}: { component: MainComponent }) => {
    const sheetThemeThreshold = 0.92;
    const desktopMediaQuery = "(min-width: 768px)";
    const modalMediaQuery = "(min-width: 640px)";

    const summaryState = useValue(component.summaryComponent.state);
    const transactionsState = useValue(component.transactionsComponent.state);

    const hasActiveFilters = 
        transactionsState.filters.allowIncome ||
        transactionsState.filters.allowOutcome ||
        transactionsState.filters.allowTransfer ||
        transactionsState.filters.accountIds.asJsReadonlyArrayView().length > 0 ||
        transactionsState.filters.categoryIds.asJsReadonlyArrayView().length > 0 ||
        transactionsState.filters.dateFrom !== null ||
        transactionsState.filters.dateTo !== null ||
        (transactionsState.filters.noteQuery !== null && transactionsState.filters.noteQuery !== "");

    const manageTransactionSlot = useValue(component.jsManageTransactionSlot);
    const manageTransactionComponent = manageTransactionSlot.instance
    const filtersSlot = useValue(component.transactionsComponent.jsFiltersSlot);
    const filtersComponent = filtersSlot.instance;
    const [isFiltersOpen, setIsFiltersOpen] = useState(false);
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

    useLayoutEffect(() => {
        if (filtersComponent) {
            setIsFiltersOpen(true);
        }
    }, [filtersComponent]);

    useActions(component, (action) => {
        when(action)
            .is(MainAction.HideManageTransaction, () => {
                setIsManageOpen(false);
            })
            .run()
    });

    useEffect(() => {
        const unsubscribe = component.transactionsComponent.subscribeActions((action: any) => {
            when(action)
                .is(TransactionsAction.HideFilters, () => {
                    setIsFiltersOpen(false);
                })
                .run();
        });
        return () => unsubscribe();
    }, [component.transactionsComponent]);

    useEffect(() => {
        if (!isManageOpen && manageTransactionComponent) {
            const timer = setTimeout(() => {
                component.setIsManageTransactionOpen(false);
            }, 350);
            return () => clearTimeout(timer);
        }
    }, [isManageOpen, manageTransactionComponent]);

    useEffect(() => {
        if (!isFiltersOpen && filtersComponent) {
            const timer = setTimeout(() => {
                component.transactionsComponent.setIsFiltersOpen(false);
            }, 350);
            return () => clearTimeout(timer);
        }
    }, [isFiltersOpen, filtersComponent]);

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
            className="fixed inset-0 bg-brand-primary pt-[env(safe-area-inset-top)] md:relative md:min-h-screen md:bg-surface-base md:pt-0"
        >
            <main className="relative h-full w-full md:mx-auto md:flex md:min-h-screen md:max-w-6xl md:flex-col md:px-6 md:py-6">
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
                            hasActiveFilters={hasActiveFilters}
                            onEditClick={(transactionId) => {
                                component.openTransactionToEdit(transactionId);
                            }}
                            onLoadMore={() => component.transactionsComponent.intent(TransactionsIntent.LoadMore)}
                            onFilterClick={() => component.transactionsComponent.setIsFiltersOpen(true)}
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
                                hasActiveFilters={hasActiveFilters}
                                onEditClick={(transactionId) => {
                                    component.openTransactionToEdit(transactionId);
                                }}
                                onFilterClick={() => component.transactionsComponent.setIsFiltersOpen(true)}
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

            {!useDesktopModal && (
                <BottomSheet
                    containerEl={themeElement}
                    open={isFiltersOpen}
                    onOpenChange={setIsFiltersOpen}
                    onAnimationEnd={(isOpen) => {
                        if (!isOpen) {
                            component.transactionsComponent.setIsFiltersOpen(false);
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
                    {filtersComponent && (
                        <div className="flex flex-col h-full w-full relative">
                            <div className="flex items-center justify-between px-6 py-4">
                                <div className="flex items-center gap-2">
                                    <div className="bg-brand-primary w-8 h-8 rounded-full flex items-center justify-center">
                                        <svg className="w-4 h-4 text-brand-on-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                        </svg>
                                    </div>
                                    <span className="text-xl font-bold text-text-primary">Фильтры</span>
                                </div>
                                <button
                                    onClick={() => setIsFiltersOpen(false)}
                                    className="p-2 rounded-xl text-text-secondary transition-colors hover:bg-surface-muted hover:text-text-primary"
                                >
                                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                            <Filters component={filtersComponent} />
                        </div>
                    )}
                </BottomSheet>
            )}

            {useDesktopModal && isFiltersOpen && filtersComponent && createPortal(
                <div
                    className="fixed inset-0 z-[100] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[2px]"
                    onClick={() => setIsFiltersOpen(false)}
                >
                    <div
                        className="flex max-h-[calc(100vh-4rem)] w-full max-w-2xl min-h-0 flex-col rounded-3xl bg-surface-sheet shadow-2xl"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="flex justify-between px-6 pb-0 pt-5">
                            <div className="flex items-center gap-2">
                                <div className="bg-brand-primary w-8 h-8 rounded-full flex items-center justify-center">
                                    <svg className="w-4 h-4 text-brand-on-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                    </svg>
                                </div>
                                <span className="text-xl font-bold text-text-primary">Фильтры</span>
                            </div>
                            <button
                                aria-label="Закрыть"
                                className="rounded-xl p-2 text-text-secondary transition-colors hover:bg-surface-muted hover:text-text-primary"
                                onClick={() => setIsFiltersOpen(false)}
                                type="button"
                            >
                                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                        <div className="min-h-0 flex-1 overflow-y-auto px-1 pb-4">
                            <Filters component={filtersComponent} />
                        </div>
                    </div>
                </div>,
                document.body
            )}
        </div>
    );
};


export default Main;
