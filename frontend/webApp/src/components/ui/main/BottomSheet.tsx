import {Drawer} from "vaul";
import {type ReactNode, useEffect, useMemo, useRef, useState} from "react";


const PURPLE_RGB: [number, number, number] = [79, 57, 246];
const WHITE_RGB: [number, number, number] = [255, 255, 255];
const TOP_BLEND_ZONE_PX = 100;

const BottomSheet = ({
                         children,
                         containerEl,
                         portalContainer,
                     }: {
    children: ReactNode;
    containerEl: HTMLElement | undefined;
    portalContainer: HTMLElement | null;
}) => {
    const [isOpen, setIsOpen] = useState(true);
    const [snapPoint, setSnapPoint] = useState<number | string | null>(0.5);
    const [radius, setRadius] = useState(32);
    const contentRef = useRef<HTMLDivElement | null>(null);

    const snapPoints = useMemo(() => [0.5, 1], []);

    useEffect(() => {
        let frameId = 0;
        const metaTag = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');

        const updateByPosition = () => {
            const top = contentRef.current?.getBoundingClientRect().top ?? TOP_BLEND_ZONE_PX;
            const progress = Math.max(0, Math.min(1, (TOP_BLEND_ZONE_PX - top + 70) / TOP_BLEND_ZONE_PX));

            const mixed: [number, number, number] = [
                Math.round(PURPLE_RGB[0] + (WHITE_RGB[0] - PURPLE_RGB[0]) * progress),
                Math.round(PURPLE_RGB[1] + (WHITE_RGB[1] - PURPLE_RGB[1]) * progress),
                Math.round(PURPLE_RGB[2] + (WHITE_RGB[2] - PURPLE_RGB[2]) * progress),
            ];

            const color = `rgb(${mixed[0]},${mixed[1]},${mixed[2]})`;
            setRadius(32 * (1 - progress));

            if (containerEl) {
                containerEl.style.backgroundColor = color;
                document.documentElement.style.backgroundColor = color;
                document.body.style.backgroundColor = color;
            }

            frameId = requestAnimationFrame(updateByPosition);
        };

        frameId = requestAnimationFrame(updateByPosition);
        return () => cancelAnimationFrame(frameId);
    }, [containerEl]);

    if (!portalContainer) {
        return null;
    }

    return (
        <Drawer.Root
            open={isOpen}
            onOpenChange={(nextOpen: boolean) => {
                if (!nextOpen) return;
                setIsOpen(nextOpen);
            }}
            dismissible={false}
            modal={false}
            snapPoints={snapPoints}
            activeSnapPoint={snapPoint}
            setActiveSnapPoint={setSnapPoint}
        >
            <Drawer.Portal container={portalContainer}>
                <Drawer.Content
                    ref={contentRef}
                    className="pointer-events-auto fixed inset-x-0 bottom-0 z-30 bg-white outline-none h-[100svh] max-h-[100svh]"
                    style={{
                        marginTop: 'env(safe-area-inset-top)',
                        borderTopLeftRadius: `${radius}px`,
                        borderTopRightRadius: `${radius}px`,
                    }}
                >
                    <Drawer.Handle className="w-10 h-1 bg-slate-200 rounded-full mx-auto mt-4"/>
                    <div
                        className="overflow-y-auto"
                        style={{
                            paddingBottom: 'calc(70px + env(safe-area-inset-bottom))',
                        }}
                    >
                        {children}
                    </div>
                </Drawer.Content>
            </Drawer.Portal>
        </Drawer.Root>
    );
}

export default BottomSheet;