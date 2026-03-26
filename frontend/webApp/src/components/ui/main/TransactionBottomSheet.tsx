import {Sheet, SheetRef} from 'react-modal-sheet';
import {useRef, useState} from 'react';
import {changeMetaThemeColor} from "../../../styles/changeMetaThemeColor.ts";
import {useMotionValue, useMotionValueEvent, useTransform} from "framer-motion";


const BottomSheet = ({children, containerEl}: { children: React.ReactNode, containerEl: HTMLElement | undefined }) => {


    const [isOpen, setOpen] = useState(true);
    const [isFullyOpened, setIsFullyOpened] = useState(false);
    const lastThemeColor = useRef<"F9FAFF" | "#4F39F6">("#4F39F6");
    const ref = useRef<SheetRef>(null);

    const fallbackY = useMotionValue(0);

    const motionY = ref.current?.y ?? fallbackY;


    const updateFullyState = (y: number) => {
        setIsFullyOpened(y < 5);
        const nextColor = isFullyOpened ? "F9FAFF" : "#4F39F6";
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

    const snapPoints = [0, 0.59, 1];

    return (
        <Sheet
            ref={ref}
            isOpen={isOpen}
            onSnap={handleSnap}
            onClose={() => setOpen(false)}
            snapPoints={snapPoints}
            initialSnap={1}
            disableDismiss={true}
            style={{
                zIndex: 10,
                marginTop: 'env(safe-area-inset-top)',
            }}
            detent={"full"}
            className="sm:hidden"
            mountPoint={containerEl}
        >
            <Sheet.Container
                style={{
                    backgroundColor: "#F9FAFF",
                    // borderRadius: 0
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
                        touchAction: !isFullyOpened ? "none": "auto",
                        paddingBottom: 'calc(70px + env(safe-area-inset-bottom))', // TODO: щас 70 стоит на рандом (нужно чекать высоту bottomBar)
                    }}
                    // scrollClassName="no-scrollbar" TODO: ??
                >
                    {children}
                </Sheet.Content>
            </Sheet.Container>
            <Sheet.Backdrop
                style={{backgroundColor: 'transparent', pointerEvents: 'none'}}
            />
        </Sheet>
    );
}

export default BottomSheet;