import { Drawer } from "vaul";
import { useEffect, useMemo, useRef, useState } from "react";

const PURPLE_RGB: [number, number, number] = [79, 57, 246];
const WHITE_RGB: [number, number, number] = [255, 255, 255];
const BUCKETS = 28;

const rgbToHex = (r: number, g: number, b: number) =>
    `#${[r, g, b].map(x => x.toString(16).padStart(2, '0')).join('')}`;

const isStandalone = () =>
    window.matchMedia('(display-mode: standalone)').matches || (window.navigator as any).standalone;

const createTintBar = (color: string) => {
    const bar = document.createElement("div");
    Object.assign(bar.style, {
        position: "fixed", top: "0", left: "0", right: "0", zIndex: "9999",
        height: "max(env(safe-area-inset-top), 6px)", backgroundColor: color, pointerEvents: "none"
    });
    return bar;
};

const BottomSheet = ({ children, containerEl, portalContainer }: any) => {
    const [snapPoint, setSnapPoint] = useState<number | string | null>(0.5);
    const [radius, setRadius] = useState(32);
    const [isAtTop, setIsAtTop] = useState(true);
    const contentRef = useRef<HTMLDivElement>(null);

    const snapPoints = useMemo(() => [ 0.5, 1], []);

    useEffect(() => {
        let frameId: number;
        let tintBarEl: HTMLDivElement | null = null;
        let lastBucket = -1;
        const isIOSBrowser = !isStandalone() && /iPhone|iPad/.test(navigator.userAgent);

        const update = () => {
            if (!contentRef.current) return;
            const rect = contentRef.current.getBoundingClientRect();
            const winH = window.innerHeight;
            const marginTop = parseFloat(getComputedStyle(contentRef.current).marginTop) || 0;

            const halfTop = winH * 0.5;
            let progress = Math.max(0, Math.min(1, (halfTop - rect.top) / (halfTop - marginTop)));

            const rgb: [number, number, number] = [
                Math.round(PURPLE_RGB[0] + (WHITE_RGB[0] - PURPLE_RGB[0]) * progress),
                Math.round(PURPLE_RGB[1] + (WHITE_RGB[1] - PURPLE_RGB[1]) * progress),
                Math.round(PURPLE_RGB[2] + (WHITE_RGB[2] - PURPLE_RGB[2]) * progress),
            ];

            const color = `rgb(${rgb[0]},${rgb[1]},${rgb[2]})`;
            const hex = rgbToHex(rgb[0], rgb[1], rgb[2]);

            setRadius(32 * (1 - progress));

            if (containerEl) containerEl.style.backgroundColor = color;
            document.documentElement.style.backgroundColor = color;

            const meta = document.querySelector('meta[name="theme-color"]');
            if (meta) meta.setAttribute("content", hex);

            if (isIOSBrowser) {
                const bucket = Math.round(progress * BUCKETS);
                if (!tintBarEl) {
                    tintBarEl = createTintBar(color);
                    document.body.appendChild(tintBarEl);
                } else if (bucket !== lastBucket) {
                    const next = createTintBar(color);
                    tintBarEl.replaceWith(next);
                    tintBarEl = next;
                    lastBucket = bucket;
                }
            }

            frameId = requestAnimationFrame(update);
        };

        frameId = requestAnimationFrame(update);
        return () => {
            cancelAnimationFrame(frameId);
            tintBarEl?.remove();
        };
    }, [containerEl]);

    return (
        <Drawer.Root
            snapPoints={snapPoints}
            activeSnapPoint={snapPoint}
            setActiveSnapPoint={setSnapPoint}
            dismissible={false}
            modal={false}
            open={true}
        >
            <Drawer.Portal container={portalContainer}>
                <Drawer.Content
                    ref={contentRef}
                    className="fixed inset-x-0 bottom-0 z-30 bg-white outline-none h-[100svh] flex flex-col"
                    style={{
                        marginTop: 'env(safe-area-inset-top)',
                        borderTopLeftRadius: `${radius}px`,
                        borderTopRightRadius: `${radius}px`,
                    }}
                >
                    <Drawer.Handle className="w-10 h-1 bg-slate-200 rounded-full mx-auto mt-4 shrink-0" />
                    <div
                        className="flex-1 overflow-y-auto"
                        onScroll={(e) => {
                            const st = e.currentTarget.scrollTop;
                            setIsAtTop(st <= 0);
                        }}
                        style={{
                            paddingBottom: 'calc(70px + env(safe-area-inset-bottom))',
                            touchAction: snapPoint === 1 ? (isAtTop ? 'pan-x pan-down' : 'auto') : 'none'
                        }}
                    >
                        {children}
                    </div>
                </Drawer.Content>
            </Drawer.Portal>
        </Drawer.Root>
    );
};

export default BottomSheet;