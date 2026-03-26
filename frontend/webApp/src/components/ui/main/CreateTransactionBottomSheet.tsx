import {Sheet, SheetRef} from 'react-modal-sheet';
import {useRef, useState} from 'react';
import {changeMetaThemeColor} from "../../../styles/changeMetaThemeColor.ts";
import {useMotionValue, useMotionValueEvent, useTransform} from "framer-motion";
import CreateTransactionContent from "./CreateTransactionContent.tsx";

interface CreateTransactionModalProps {
    isOpen: boolean;
    onClose: () => void;
    containerEl?: HTMLElement;
}

const CreateTransactionBottomSheet = ({isOpen, onClose, containerEl}: CreateTransactionModalProps) => {
    const [isFullyOpened, setIsFullyOpened] = useState(false);
    const lastThemeColor = useRef<"#EBEBF0" | "#4F39F6">("#4F39F6");
    const ref = useRef<SheetRef>(null);

    const fallbackY = useMotionValue(0);
    const motionY = ref.current?.y ?? fallbackY;

    const updateFullyState = (y: number) => {
        setIsFullyOpened(y < 5);
        const nextColor = isFullyOpened ? "#EBEBF0" : "#4F39F6";
        if (lastThemeColor.current !== nextColor) {
            lastThemeColor.current = nextColor;
            changeMetaThemeColor(containerEl, nextColor);
        }
    }

    const borderRadius = useTransform(motionY, [0, 40], [0, 32]);

    useMotionValueEvent(motionY, "change", (y) => {
        updateFullyState(y);
    });

    const handleSnap = () => {
        updateFullyState(motionY.get())
    };

    const snapPoints = [0, 1];

    return (
        <Sheet
            ref={ref}
            isOpen={isOpen}
            onSnap={handleSnap}
            onClose={onClose}
            snapPoints={snapPoints}
            initialSnap={1}
            style={{
                zIndex: 60,
                marginTop: 'env(safe-area-inset-top)',
            }}
            detent={"full"}
            className="sm:hidden"
            mountPoint={typeof document !== 'undefined' ? document.body : undefined}
        >
            <Sheet.Container
                style={{
                    backgroundColor: "#EBEBF0",
                    borderTopLeftRadius: borderRadius,
                    borderTopRightRadius: borderRadius
                }}
            >
                <Sheet.Header>
                    <div className="w-10 h-1 bg-slate-200 rounded-full mx-auto mt-4"/>
                </Sheet.Header>

                <Sheet.Content
                    disableScroll={!isFullyOpened}
                    scrollStyle={{
                        touchAction: !isFullyOpened ? "none" : "auto",
                        paddingBottom: 'calc(env(safe-area-inset-bottom))',
                    }}
                    scrollClassName="no-scrollbar"
                >
                <CreateTransactionContent/>
                </Sheet.Content>
            </Sheet.Container>
            <Sheet.Backdrop
                style={{backgroundColor: 'transparent', pointerEvents: 'auto'}}
                onTap={onClose}
            />
        </Sheet>
    );
}

export default CreateTransactionBottomSheet;