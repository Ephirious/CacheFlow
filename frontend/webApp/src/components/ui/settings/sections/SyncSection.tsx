import {FiDownload, FiLogIn, FiLogOut, FiRefreshCw, FiCheckCircle, FiAlertCircle, FiCloudOff} from "react-icons/fi";
import { SyncOverviewComponent, SyncOverviewIntent, SyncOverviewState, SyncStatus } from "k2ts";
import {useValue, when} from "interop";

const SyncSection = ({component}: { component: SyncOverviewComponent }) => {
    const state = useValue(component.state)

    return (
        <div className="mx-6 flex flex-col gap-4 rounded-xl border border-border-default p-6 lg:mx-auto lg:w-full lg:max-w-6xl">
            <div className="flex items-center justify-between">
                <h2 className="text-lg font-semibold text-text-primary">Синхронизация</h2>
                {when(state)
                    .on(SyncOverviewState.Authenticated, (auth) => {
                        if (auth.syncStatus === SyncStatus.Ok) {
                            return <div className="flex items-center gap-1.5 text-brand-primary"><FiCheckCircle className="h-4 w-4"/> <span className="text-sm font-medium">Синхронизировано</span></div>;
                        }
                        if (auth.syncStatus === SyncStatus.InProcess) {
                            return <div className="flex items-center gap-1.5 text-text-secondary"><FiRefreshCw className="h-4 w-4 animate-spin"/> <span className="text-sm font-medium">Синхронизация...</span></div>;
                        }
                        return <div className="flex items-center gap-1.5 text-state-danger"><FiAlertCircle className="h-4 w-4"/> <span className="text-sm font-medium">Ошибка</span></div>;
                    })
                    .otherwise(() => <div className="flex items-center gap-1.5 text-text-secondary" title="Не синхронизировано"><FiCloudOff className="h-4 w-4"/></div>)}
            </div>

            <div className="flex flex-col gap-3">
                {when(state)
                    .is(SyncOverviewState.NotAuthenticated, () => (
                        <button
                            onClick={() => component.onAuthenticateClick()}
                            className="flex w-full items-center gap-3 rounded-lg border border-border-subtle bg-surface-base px-4 py-3 text-base cursor-pointer hover:bg-surface-muted active:scale-[0.98] transition-all"
                            type="button"
                        >
                            <span className="rounded-xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                                <FiLogIn className="h-5 w-5"/>
                            </span>
                            <p className="text-text-primary font-medium">Вход/Регистрация</p>
                        </button>
                    ))
                    .on(SyncOverviewState.Authenticated, (auth) => (
                        <>
                            <div className="mb-2 flex flex-col gap-1 rounded-lg bg-surface-muted p-4">
                                <span className="text-sm font-medium text-text-primary">{auth.name}</span>
                                <span className="text-sm text-text-secondary">{auth.email}</span>
                            </div>
                            <button
                                onClick={() => component.intent(SyncOverviewIntent.ForceSync)}
                                className="flex w-full items-center gap-3 rounded-lg border border-border-subtle bg-surface-base px-4 py-3 text-base cursor-pointer hover:bg-surface-muted active:scale-[0.98] transition-all"
                                type="button"
                            >
                                <span className="rounded-xl bg-text-primary/5 p-2 text-text-primary">
                                    <FiRefreshCw className="h-5 w-5"/>
                                </span>
                                <p className="text-text-primary font-medium">Принудительная синхронизация</p>
                            </button>
                            <button
                                onClick={() => component.intent(SyncOverviewIntent.ExportCSV)}
                                className="flex w-full items-center gap-3 rounded-lg border border-border-subtle bg-surface-base px-4 py-3 text-base cursor-pointer hover:bg-surface-muted active:scale-[0.98] transition-all"
                                type="button"
                            >
                                <span className="rounded-xl bg-text-primary/5 p-2 text-text-primary">
                                    <FiDownload className="h-5 w-5"/>
                                </span>
                                <p className="text-text-primary font-medium">Экспорт в CSV</p>
                            </button>
                            <div className="flex items-start gap-2 rounded-xl bg-state-danger/10 p-3 text-sm text-state-danger border border-state-danger/20 mt-2">
                                <FiAlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
                                <p>
                                    При выходе из аккаунта все данные на этом устройстве будут удалены.
                                </p>
                            </div>
                            <button
                                onClick={() => component.intent(SyncOverviewIntent.Logout)}
                                className="flex w-full items-center gap-3 rounded-lg border border-state-danger/30 bg-surface-base px-4 py-3 text-base cursor-pointer hover:bg-state-danger/10 active:scale-[0.98] transition-all"
                                type="button"
                            >
                                <span className="rounded-xl bg-state-danger/10 p-2 text-state-danger">
                                    <FiLogOut className="h-5 w-5"/>
                                </span>
                                <p className="text-state-danger font-medium">Выйти из аккаунта</p>
                            </button>
                        </>
                    ))
                    .is(SyncOverviewState.Loading, () => (
                        <div className="flex justify-center p-4">
                            <FiRefreshCw className="h-6 w-6 animate-spin text-text-secondary"/>
                        </div>
                    ))
                    .run()}
            </div>
        </div>
    );
};

export default SyncSection;
