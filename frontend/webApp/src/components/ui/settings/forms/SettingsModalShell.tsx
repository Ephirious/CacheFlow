import {ReactNode, useEffect, useState} from "react";
import {FiX} from "react-icons/fi";
import {createPortal} from "react-dom";
import {motion} from "framer-motion";

interface SettingsModalShellProps {
    open: boolean;
    title: string;
    children: ReactNode;
    onClose: () => void;
}

const SettingsModalShell = ({open, title, children, onClose}: SettingsModalShellProps) => {
    const [mounted, setMounted] = useState(false);

    useEffect(() => {
        setMounted(true);
    }, []);

    if (!open || !mounted) return null;

    return createPortal(
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[2px]">
            <motion.div 
                layout
                transition={{ type: "spring", bounce: 0, duration: 0.3 }}
                className="flex w-full max-w-lg flex-col gap-4 rounded-2xl bg-surface-sheet p-6 shadow-xl"
            >
                <motion.div layout className="flex items-center justify-between">
                    <h3 className="text-lg font-bold text-text-primary">{title}</h3>
                    <button
                        aria-label="Закрыть"
                        className="rounded-lg p-1 text-text-primary hover:bg-surface-base/60"
                        onClick={onClose}
                        type="button"
                    >
                        <FiX className="h-5 w-5"/>
                    </button>
                </motion.div>
                <motion.div layout>
                    {children}
                </motion.div>
            </motion.div>
        </div>,
        document.body
    );
};

export default SettingsModalShell;
