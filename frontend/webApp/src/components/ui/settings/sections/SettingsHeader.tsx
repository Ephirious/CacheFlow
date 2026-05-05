import {FiMoon, FiSun, FiMonitor, FiSettings} from "react-icons/fi";
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

const SettingsHeader = ({curTheme, tab, onTabChange, onThemeClick}: SettingsHeaderProps) => {
    const renderThemeIcon = () => {
        if (curTheme === AppTheme.Dark) return <FiMoon className="h-6 w-6"/>;
        if (curTheme === AppTheme.Light) return <FiSun className="h-6 w-6"/>;
        return <FiMonitor className="h-6 w-6"/>;
    };

    return (
        <div className="flex flex-col gap-6 border-b border-border-subtle bg-surface-base p-6 pt-[calc(env(safe-area-inset-top)+24px)] transition-colors duration-300 lg:mx-auto lg:mt-6 lg:w-full lg:max-w-6xl lg:rounded-3xl lg:border lg:pt-6">
            <div className="flex items-center justify-between gap-6">
                <div className="flex items-center gap-3">
                    <div className="rounded-2xl bg-brand-primary-emphasis/10 p-2 text-brand-primary">
                        <FiSettings className="h-6 w-6"/>
                    </div>
                    <h1 className="text-2xl font-bold text-text-primary transition-colors duration-300">Настройки</h1>
                </div>
                <button 
                    onClick={onThemeClick}
                    className="rounded-2xl bg-surface-muted text-text-primary p-2 transition-colors duration-300 active:scale-95" 
                    type="button"
                    aria-label="Toggle Theme"
                >
                    {renderThemeIcon()}
                </button>
            </div>

            <SegmentedTabs active={tab} onChange={onTabChange} options={settingsTabs}/>
        </div>
    );
};

export default SettingsHeader;
