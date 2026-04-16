import {PiClockClockwise} from "react-icons/pi";
import {currentBalance} from "../data.ts";

const StatsHero = () => {
    return (
        <section className=" bg-brand-primary px-6 pb-4 pt-[calc(env(safe-area-inset-top)+24px)] text-brand-on-primary sm:px-6 sm:pb-6 sm:pt-[calc(env(safe-area-inset-top)+16px)]">
            <div className="mx-auto flex w-full max-w-5xl flex-col gap-3 sm:gap-4">
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
