let themeAnimId: number | null = null;
let currentRGB: [number, number, number] | null = null;

const getMetaTag = () => document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');


const toRGB = (color: any): [number, number, number] => {
    if (typeof color !== 'string') return [255, 255, 255];
    const normalized = color.trim().toLowerCase();
    if (normalized === 'white') return [255, 255, 255];
    if (normalized === 'black') return [0, 0, 0];
    let hex = normalized.replace(/[^0-9a-f]/g, '');
    if (hex.length === 3) hex = hex.split('').map(s => s + s).join('');
    if (hex.length !== 6) return [255, 255, 255];
    const n = parseInt(hex, 16);
    return [(n >> 16) & 255, (n >> 8) & 255, n & 255];
};

const easeInOutSine = (t: number): number => -(Math.cos(Math.PI * t) - 1) / 2;

export function changeMetaThemeColor(
    element: HTMLElement | undefined,
    targetColor: string,
    duration: number = 400
): void {
    const metaTag = getMetaTag();
    if (!metaTag) return;


    if (currentRGB === null) {
        currentRGB = toRGB(metaTag.content || "#ffffff");
    }

    const startRGB = [...currentRGB] as [number, number, number];
    const endRGB = toRGB(targetColor);

    if (startRGB.every((val, i) => val === endRGB[i])) return;

    if (themeAnimId) cancelAnimationFrame(themeAnimId);

    const startTime = performance.now();

    function frame(now: number) {
        const elapsed = now - startTime;
        const progress = Math.min(elapsed / duration, 1);

        const ease = easeInOutSine(progress);

        const r = (startRGB[0] + (endRGB[0] - startRGB[0]) * ease) | 0;
        const g = (startRGB[1] + (endRGB[1] - startRGB[1]) * ease) | 0;
        const b = (startRGB[2] + (endRGB[2] - startRGB[2]) * ease) | 0;

        currentRGB = [r, g, b];
        const rgbStr = `rgb(${r},${g},${b})`;


        metaTag!.content = rgbStr;
        if (element) {
            element.style.backgroundColor = rgbStr;
        }

        if (progress < 1) {
            themeAnimId = requestAnimationFrame(frame);
        } else {
            themeAnimId = null;
        }
    }

    themeAnimId = requestAnimationFrame(frame);
}