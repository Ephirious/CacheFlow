import {Sheet, SheetRef} from 'react-modal-sheet';
import {useRef, useState} from 'react';
import {changeMetaThemeColor} from "../../styles/changeMetaThemeColor.ts";


const BottomSheet = ({children}: { onFullOpen?: () => void, children: React.ReactNode }) => {
    const [isOpen, setOpen] = useState(true);

    const [currentIndex, setCurrentIndex] = useState(1);

    const ref = useRef<SheetRef>(null);
    const snapTo = (i: number) => ref.current?.snapTo(i);

    let prevIndex = 1;

    const handleSnap = (index: number) => {
        setCurrentIndex(index);

        if (index == 2) {
            changeMetaThemeColor("black")
        } else {
            changeMetaThemeColor("white")
        }
        const themeColorMetaTag = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');
        console.log(themeColorMetaTag)
    }
    return (
        <Sheet
            ref={ref}
            isOpen={isOpen}
            onClose={() => setOpen(false)}
            snapPoints={[0, 0.4, 1]}
            initialSnap={1}
            disableDismiss={true}
            style={{
                zIndex: 40
            }}
            onSnap={handleSnap}
            detent={"full"}
            className="sm:hidden"
        >
            <Sheet.Container
                style={{borderRadius: currentIndex === 1 ? "32px" : "0px", transition: "border-radius 0.5s"}}
            >
                <Sheet.Header>
                    <div className="w-10 h-1 bg-slate-200 rounded-full mx-auto mt-4"/>
                </Sheet.Header>

                <Sheet.Content dragElastic={{}}>
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