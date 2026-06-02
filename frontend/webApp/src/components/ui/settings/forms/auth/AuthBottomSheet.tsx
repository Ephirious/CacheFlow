import { AuthChild, AuthComponent, AuthOutput } from "k2ts";
import { useValue, when } from "interop";
import RegistrationForm from "./RegistrationForm.tsx";
import LoginForm from "./LoginForm.tsx";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import SettingsModalShell from "../SettingsModalShell.tsx";
import { BottomSheet } from "../../../main";
import { SegmentedTabs } from "../../primitives";

const authTabs = [
    { id: "login", label: "Вход" },
    { id: "register", label: "Регистрация" }
];

interface AuthBottomSheetProps {
    component: AuthComponent;
}

const AuthBottomSheet = ({ component }: AuthBottomSheetProps) => {
    const pages = useValue(component.childPages);
    const activeChild = pages.active;
    const isRegistrationActive = activeChild instanceof AuthChild.RegistrationChild;

    const modalMediaQuery = "(min-width: 640px)";
    const [useDesktopModal, setUseDesktopModal] = useState(() => window.matchMedia(modalMediaQuery).matches);

    useEffect(() => {
        const modalMql = window.matchMedia(modalMediaQuery);
        const handleModalChange = (event: MediaQueryListEvent) => setUseDesktopModal(event.matches);
        modalMql.addEventListener("change", handleModalChange);
        return () => modalMql.removeEventListener("change", handleModalChange);
    }, []);

    const [isOpen, setIsOpen] = useState(true);

    const handleClose = () => {
        setIsOpen(false);
        setTimeout(() => {
            component.backToSettings();
        }, 300);
    };

    const content = (
        <motion.div layout className="flex flex-col gap-6 px-1 pb-4">
            <SegmentedTabs
                active={isRegistrationActive ? "register" : "login"}
                onChange={(val) => {
                    if (val === "register") component.onOutput(AuthOutput.NavigateToRegistration);
                    else component.onOutput(AuthOutput.NavigateToLogin);
                }}
                options={authTabs}
            />
            <motion.div layout>
                {when(activeChild)
                    .on(AuthChild.RegistrationChild, (child) => <RegistrationForm component={child.component}/>)
                    .on(AuthChild.LoginChild, (child) => <LoginForm component={child.component}/>)
                    .run()}
            </motion.div>
        </motion.div>
    );

    if (useDesktopModal) {
        return (
            <SettingsModalShell
                open={isOpen}
                onClose={handleClose}
                title="Авторизация"
            >
                {content}
            </SettingsModalShell>
        );
    }

    return (
        <BottomSheet
            open={isOpen}
            onOpenChange={(open) => {
                if (!open) handleClose();
            }}
            snapPoints={[1]}
            initialSnapPoint={1}
            dismissible={true}
            modal={true}
            repositionInputs={false}
            backgroundColor="var(--color-surface-base)"
            contentPaddingBottom="calc(env(safe-area-inset-bottom))"
        >
            <div className="flex flex-col pt-4">
                <div className="px-6 pb-2 text-xl font-bold text-text-primary mb-4 text-center">Авторизация</div>
                <div className="px-6">{content}</div>
            </div>
        </BottomSheet>
    );
};

export default AuthBottomSheet;
