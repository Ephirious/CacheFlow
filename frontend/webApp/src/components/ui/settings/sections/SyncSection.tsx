import {FiDownload, FiLogIn} from "react-icons/fi";

const SyncSection = () => {
    return (
        <div className="mx-6 flex flex-col gap-4 rounded-xl border border-border-default p-6">
            <h2 className="text-lg font-semibold text-text-primary">Синхронизация</h2>
            <div className="flex flex-col gap-3">
                <button
                    className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                    type="button"
                >
                    <span className="rounded-xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiLogIn className="h-5 w-5"/>
                    </span>
                    Вход/Выход
                </button>
                <button
                    className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                    type="button"
                >
                    <span className="rounded-full bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiDownload className="h-5 w-5"/>
                    </span>
                    Экспорт в CSV
                </button>
            </div>
        </div>
    );
};

export default SyncSection;
