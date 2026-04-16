import { Drawer } from "vaul";
import { ReactNode, useEffect, useMemo, useRef, useState } from "react";
import { changeMetaThemeColor } from "../../../../styles/changeMetaThemeColor.ts";

const BUCKETS = 120;
const DEFAULT_THEME_BASE_COLOR = "var(--color-brand-primary)";

const isStandalone = () =>
    window.matchMedia('(display-mode: standalone)').matches || (window.navigator as any).standalone;

const createTintBar = (color: string) => {
    const bar = document.createElement("div");
    Object.assign(bar.style, {
        position: "fixed", top: "0", left: "0", right: "0", zIndex: "9999",
        height: "6px", backgroundColor: color, pointerEvents: "none"
    });
    return bar;
};

const resolveToRgbString = (color: string): string => {
    const probe = document.createElement("div");
    probe.style.color = color;
    document.body.appendChild(probe);
    const resolved = getComputedStyle(probe).color;
    probe.remove();
    return resolved || color;
};

const toRGB = (color: string): [number, number, number] => {
    const normalized = resolveToRgbString(color).trim().toLowerCase();

    if (normalized.startsWith("rgb")) {
        const match = normalized.match(/\d+/g);
        if (match && match.length >= 3) {
            return [Number(match[0]), Number(match[1]), Number(match[2])];
        }
    }

    const hex = normalized.replace(/[^0-9a-f]/g, "");
    const fullHex = hex.length === 3 ? hex.split("").map((x) => x + x).join("") : hex;
    if (fullHex.length !== 6) return [255, 255, 255];

    const num = parseInt(fullHex, 16);
    return [(num >> 16) & 255, (num >> 8) & 255, num & 255];
};

const isVisibleColor = (value?: string | null) => {
    if (!value) return false;
    const color = value.trim().toLowerCase();
    return color.length > 0 && color !== "transparent" && color !== "rgba(0, 0, 0, 0)";
};

type ThemeMode = "interpolate" | "fixed" | "none";

interface BottomSheetProps {
    children: ReactNode;
    containerEl?: HTMLElement;
    portalContainer?: HTMLElement | null;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
    snapPoints?: Array<number | string>;
    initialSnapPoint?: number | string | null;
    dismissible?: boolean;
    modal?: boolean;
    zIndex?: number;
    backgroundColor?: string;
    className?: string;
    themeMode?: ThemeMode;
    themeEnabled?: boolean;
    themeTargetColor?: string;
    themeBaseColor?: string;
    useCurrentThemeAsBase?: boolean;
    themeInterpolationStartThreshold?: number;
    showOverlay?: boolean;
    contentPaddingBottom?: string;
    themeTransitionDuration?: number;
    repositionInputs?: boolean;
    fixed?: boolean;
}

const setThemeColor = (containerEl: HTMLElement | undefined, color: string) => {
    if (containerEl) containerEl.style.backgroundColor = color;
    document.documentElement.style.backgroundColor = color;

    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute("content", color);
};

const BottomSheet = ({
    children,
    containerEl,
    portalContainer,
    open = true,
    onOpenChange,
    snapPoints: propSnapPoints,
    initialSnapPoint = 0.5,
    dismissible = false,
    modal = false,
    zIndex = 30,
    backgroundColor = "var(--color-surface-base)",
    className = "",
    themeMode = "none",
    themeEnabled = true,
    themeTargetColor = "var(--color-surface-base)",
    themeBaseColor,
    useCurrentThemeAsBase = false,
    themeInterpolationStartThreshold = 0,
    showOverlay = modal,
    contentPaddingBottom = 'calc(70px + env(safe-area-inset-bottom))',
    themeTransitionDuration = 240,
    repositionInputs = true,
    fixed = false,
}: BottomSheetProps) => {
    const [snapPoint, setSnapPoint] = useState<number | string | null>(initialSnapPoint);
    const [radius, setRadius] = useState(32);
    const contentRef = useRef<HTMLDivElement>(null);
    const resolvedBaseColorRef = useRef(themeBaseColor ?? DEFAULT_THEME_BASE_COLOR);

    const snapPoints = useMemo(() => propSnapPoints ?? [0.5, 1], [propSnapPoints]);

    const readCurrentTopColor = () => {
        const containerColor = containerEl ? getComputedStyle(containerEl).backgroundColor : null;
        if (isVisibleColor(containerColor)) return containerColor!;

        const htmlColor = getComputedStyle(document.documentElement).backgroundColor;
        if (isVisibleColor(htmlColor)) return htmlColor;

        const metaColor = document.querySelector<HTMLMetaElement>('meta[name="theme-color"]')?.content;
        if (isVisibleColor(metaColor)) return metaColor!;

        return DEFAULT_THEME_BASE_COLOR;
    };

    useEffect(() => {
        if (!themeEnabled || themeMode !== "interpolate" || !open) {
            const baseColor = themeBaseColor ?? resolvedBaseColorRef.current;

            if (themeEnabled && themeMode === "interpolate" && !open) {
                changeMetaThemeColor(containerEl, baseColor, themeTransitionDuration);
                document.documentElement.style.backgroundColor = baseColor;
            }
            return;
        }

        let frameId: number;
        let tintBarEl: HTMLDivElement | null = null;
        let lastBucket = -1;
        const isIOSBrowser = !isStandalone() && /iPhone|iPad/.test(navigator.userAgent);

        const resolvedBaseColor =
            themeBaseColor ?? (useCurrentThemeAsBase ? readCurrentTopColor() : DEFAULT_THEME_BASE_COLOR);
        resolvedBaseColorRef.current = resolvedBaseColor;

        const baseRGB = toRGB(resolvedBaseColor);
        const targetRGB = toRGB(themeTargetColor);
        const threshold = Math.max(0, Math.min(0.99, themeInterpolationStartThreshold));

        const mapThresholdProgress = (value: number) =>
            threshold === 0 ? value : Math.max(0, Math.min(1, (value - threshold) / (1 - threshold)));

        const update = () => {
            if (!contentRef.current) return;
            const rect = contentRef.current.getBoundingClientRect();
            const winH = window.innerHeight;
            const marginTop = parseFloat(getComputedStyle(contentRef.current).marginTop) || 0;

            const halfTop = winH * 0.5;
            let progress = Math.max(0, Math.min(1.05, (halfTop - rect.top) / (halfTop - marginTop - 10)));
            const delayedProgress = mapThresholdProgress(progress);

            const rgb: [number, number, number] = [
                Math.round(baseRGB[0] + (targetRGB[0] - baseRGB[0]) * delayedProgress),
                Math.round(baseRGB[1] + (targetRGB[1] - baseRGB[1]) * delayedProgress),
                Math.round(baseRGB[2] + (targetRGB[2] - baseRGB[2]) * delayedProgress),
            ];

            const color = `rgb(${rgb[0]},${rgb[1]},${rgb[2]})`;
            setRadius(32 * (1 - delayedProgress));

            setThemeColor(containerEl, color);

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
    }, [
        containerEl,
        open,
        themeBaseColor,
        themeEnabled,
        themeMode,
        themeTargetColor,
        themeTransitionDuration,
        useCurrentThemeAsBase,
        themeInterpolationStartThreshold,
    ]);

    useEffect(() => {
        if (!themeEnabled || themeMode === "interpolate" || themeMode === "none") return;

        const baseColor = themeBaseColor ?? resolvedBaseColorRef.current;

        if (open && themeMode === "fixed") {
            setThemeColor(containerEl, themeTargetColor);
            return;
        }

        if (!open) {
            setThemeColor(containerEl, baseColor);
        }
    }, [containerEl, open, themeBaseColor, themeEnabled, themeMode, themeTargetColor]);

    useEffect(() => {
        if (typeof snapPoint === "number") {
            const normalized = Math.max(0, Math.min(1, snapPoint));
            const threshold = Math.max(0, Math.min(0.99, themeInterpolationStartThreshold));
            const delayed = threshold === 0
                ? normalized
                : Math.max(0, Math.min(1, (normalized - threshold) / (1 - threshold)));
            setRadius(32 * (1 - delayed));
        }
    }, [snapPoint, themeInterpolationStartThreshold]);

    return (
        <Drawer.Root
            snapPoints={snapPoints}
            activeSnapPoint={snapPoint}
            setActiveSnapPoint={setSnapPoint}
            dismissible={dismissible}
            modal={modal}
            repositionInputs={repositionInputs}
            fixed={fixed}
            open={open}
            onOpenChange={onOpenChange}
        >
            <Drawer.Portal container={portalContainer ?? undefined}>
                {showOverlay && <Drawer.Overlay className="fixed inset-0 bg-black/0" style={{ zIndex }} />}
                <Drawer.Content
                    ref={contentRef}
                    className={`fixed inset-x-0 bottom-0 outline-none h-[100svh] flex flex-col ${className}`.trim()}
                    style={{
                        marginTop: 'env(safe-area-inset-top)',
                        borderTopLeftRadius: `${radius}px`,
                        borderTopRightRadius: `${radius}px`,
                        backgroundColor,
                        zIndex,
                    }}
                >
                    <Drawer.Handle className="w-10 h-1 bg-border-handle rounded-full mx-auto mt-4 shrink-0" />
                    <div
                        className="flex-1 py-2.5 overflow-y-auto"
                        style={{
                            paddingBottom: contentPaddingBottom,
                            touchAction: 'auto',
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
