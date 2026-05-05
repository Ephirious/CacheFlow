import {PiClockClockwise} from "react-icons/pi";

interface StatsHeroProps {
    currentBalance: string;
}

const StatsHero = ({currentBalance}: StatsHeroProps) => {
    return (
        <section className="bg-brand-primary px-4 pb-4 pt-[calc(env(safe-area-inset-top)+24px)] text-brand-on-primary sm:px-6 sm:pb-6 sm:pt-[calc(env(safe-area-inset-top)+16px)] lg:bg-surface-base lg:pb-0 lg:pt-6">
            <div className="mx-auto flex w-full max-w-6xl flex-col gap-3 sm:gap-4 lg:rounded-3xl lg:bg-brand-primary lg:px-6 lg:py-8">
                <div className="flex items-center gap-2.5">
                    <div className="rounded-xl bg-brand-on-primary/15 p-1.5 sm:p-2">
                        <PiClockClockwise className="h-3.5 w-3.5 sm:h-4 sm:w-4"/>
                    </div>
                    <h1 className="text-2xl font-bold sm:text-3xl">Статистика</h1>
                </div>
                <div>
                    <p className="text-xs text-brand-on-primary/70 sm:text-sm">Текущий баланс</p>
                    <p className="mt-1 text-3xl font-bold sm:text-5xl">{currentBalance}</p>
                </div>
            </div>
        </section>
    );
};

export default StatsHero;
