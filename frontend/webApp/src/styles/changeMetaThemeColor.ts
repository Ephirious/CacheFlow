export function changeMetaThemeColor(newColor: string): void {
    const themeColorMetaTag = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');

    if (themeColorMetaTag) {
        themeColorMetaTag.content = newColor;
    }

    if (document.body) {
        document.body.style.backgroundColor = newColor;
    }
}

