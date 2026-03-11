import {RootChild, RootComponent, RootOutput} from "k2ts";


interface RootTabBarProps {
    component: RootComponent;
    activeChild: RootChild
}

const RootTabBar = ({component, activeChild}: RootTabBarProps) => {
    const getButtonStyle = (isActive: boolean): React.CSSProperties => ({
        padding: "8px 16px",
        margin: "0 4px",
        cursor: "pointer",
        borderRadius: "4px",
        border: "1px solid lightgray",
        backgroundColor: isActive ? "royalblue" : "white",
        color: isActive ? "white" : "black",
        transition: "all 0.2s ease"
    });

    return <div style={{display: "flex", flexDirection: "row", alignItems: "center"}}>
        <button
            style={getButtonStyle(activeChild instanceof RootChild.MainChild)}
            onClick={() => {
                component.onOutput(RootOutput.NavigateToMain)
            }}
        >
            Главная
        </button>
        <button
            style={getButtonStyle(activeChild instanceof RootChild.StatsChild)}
            onClick={() => {
                component.onOutput(RootOutput.NavigateToStats)
            }}
        >
            Статистика
        </button>
        <button
            style={getButtonStyle(activeChild instanceof RootChild.SettingsChild)}
            onClick={() => {
                component.onOutput(RootOutput.NavigateToSettings)
            }}
        >
            Настройки
        </button>
        <button
            style={getButtonStyle(activeChild instanceof RootChild.InteropSampleFlowChild)}
            onClick={() => {
                component.onOutput(RootOutput.NavigateToInteropTest)
            }}
        >
            ИнтеропТест
        </button>
    </div>
}

export default RootTabBar;