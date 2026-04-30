import {FiMoon, FiSettings} from "react-icons/fi";
import {SegmentedTabs} from "../primitives";
import {SettingsTab} from "../types.ts";
import {settingsTabs} from "../data.tsx";
import { AppTheme } from "k2ts";

interface SettingsHeaderProps {
    curTheme: AppTheme;
    tab: SettingsTab;
    onThemeClick: () => void;
    onTabChange: (tab: SettingsTab) => void;
}

const SettingsHeader = ({tab, onTabChange}: SettingsHeaderProps) => {
    return (
        <div className="flex flex-col gap-6 border-b border-border-subtle bg-surface-base p-6 pt-[calc(env(safe-area-inset-top)+24px)]">
            <div className="flex items-center justify-between gap-6">
                <div className="flex items-center gap-3">
                    <div className="rounded-2xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiSettings className="h-6 w-6"/>
                    </div>
                    <h1 className="text-2xl font-bold">Настройки</h1>
                </div>
                <button className="rounded-2xl bg-surface-muted p-2" type="button">
                    {/*TODO: здесь можно по curTheme анимировать кнопку*/}
                    {/*TODO: вызывать onThemeClick*/}
                    <FiMoon className="h-6 w-6"/>
                </button>
            </div>

            <SegmentedTabs active={tab} onChange={onTabChange} options={settingsTabs}/>
        </div>
    );
};

export default SettingsHeader;
