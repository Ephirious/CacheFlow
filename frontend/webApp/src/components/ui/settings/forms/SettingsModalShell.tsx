import {ReactNode} from "react";
import {FiX} from "react-icons/fi";

interface SettingsModalShellProps {
    open: boolean;
    title: string;
    children: ReactNode;
    onClose: () => void;
}

const SettingsModalShell = ({open, title, children, onClose}: SettingsModalShellProps) => {
    if (!open) return null;

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[2px]">
            <div className="flex w-full max-w-lg flex-col gap-4 rounded-2xl bg-surface-sheet p-6 shadow-xl">
                <div className="flex items-center justify-between">
                    <h3 className="text-lg font-bold text-text-primary">{title}</h3>
                    <button
                        aria-label="Закрыть"
                        className="rounded-lg p-1 text-text-primary hover:bg-surface-base/60"
                        onClick={onClose}
                        type="button"
                    >
                        <FiX className="h-5 w-5"/>
                    </button>
                </div>
                {children}
            </div>
        </div>
    );
};

export default SettingsModalShell;
