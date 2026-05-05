import {FiDownload, FiLogIn} from "react-icons/fi";

const SyncSection = () => {
    return (
        <div className="mx-6 flex flex-col gap-4 rounded-xl border border-border-default p-6 lg:mx-auto lg:w-full lg:max-w-6xl">
            <h2 className="text-lg font-semibold text-text-primary">Синхронизация</h2>
            <div className="flex flex-col gap-3">
                <button
                    className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                    type="button"
                >
                    <span className="rounded-xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiLogIn className="h-5 w-5"/>
                    </span>
                    <p className="text-text-primary">Вход/Выход</p>
                </button>
                <button
                    className="flex w-full items-center gap-2 rounded-lg border border-border-subtle bg-surface-base px-3 py-3 text-base"
                    type="button"
                >
                    <span className="rounded-full bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiDownload className="h-5 w-5"/>
                    </span>
                    <p className="text-text-primary">Экспорт в CSV</p>
                </button>
            </div>
        </div>
    );
};

export default SyncSection;
