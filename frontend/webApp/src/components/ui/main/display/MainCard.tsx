import {LuWallet} from "react-icons/lu";
import { AccountCard } from "./index";
import {BigDecimal, Account} from "k2ts";
import {useEffect, useMemo, useRef, useState} from "react";

interface MainCardData {
    accounts: readonly Account[],
    balance: BigDecimal,
    percentage: BigDecimal
}

const MainCard = ({data}: { data: MainCardData }) => {
    const displayBalance = data.balance.prettyString();
    const accountsList = data.accounts;
    const pageSize = 2;
    const [activePage, setActivePage] = useState(0);
    const carouselRef = useRef<HTMLDivElement | null>(null);

    const accountsPages = useMemo(() => {
        if (accountsList.length <= pageSize) {
            return [accountsList];
        }

        const pages: Account[][] = [];
        for (let i = 0; i < accountsList.length; i += pageSize) {
            pages.push(accountsList.slice(i, i + pageSize) as Account[]);
        }

        return pages;
    }, [accountsList]);

    const shouldShowCarousel = accountsList.length > pageSize;

    useEffect(() => {
        if (activePage > accountsPages.length - 1) {
            setActivePage(Math.max(accountsPages.length - 1, 0));
        }
    }, [accountsPages.length, activePage]);

    const scrollToPage = (page: number) => {
        const node = carouselRef.current;
        if (!node) return;

        node.scrollTo({
            left: page * node.clientWidth,
            behavior: "smooth"
        });
        setActivePage(page);
    };

    const handleCarouselScroll = () => {
        const node = carouselRef.current;
        if (!node) return;

        const page = Math.round(node.scrollLeft / node.clientWidth);
        if (page !== activePage) {
            setActivePage(page);
        }
    };

    return (
        <div className="w-full p-0">
            <div className="
                flex flex-col bg-brand-primary gap-6
                w-full p-6
                sm:rounded-3xl sm:p-10
            ">
                <div className="flex gap-3 items-center">
                    <div className="p-2.5 bg-brand-on-primary/20 rounded-2xl">
                        <LuWallet className="w-7 h-7 stroke-brand-on-primary"/>
                    </div>
                    <h1 className="text-2xl sm:text-3xl text-brand-on-primary font-bold">
                        <span className="sm:hidden">CacheFlow</span>
                        <span className="hidden sm:inline text-3xl">Добро пожаловать!</span>
                    </h1>
                </div>

                <div className="flex flex-col gap-1">
                    <div className="text-sm text-brand-on-primary/70 font-medium">Общий баланс</div>
                    <div className="text-5xl sm:text-6xl font-bold text-brand-on-primary">
                        {displayBalance.toString()} ₽
                    </div>

                </div>
                {shouldShowCarousel ? (
                    <>
                        <div
                            ref={carouselRef}
                            onScroll={handleCarouselScroll}
                            className="
                                mt-2 flex overflow-x-auto snap-x snap-mandatory
                                [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden
                                sm:hidden
                            "
                            style={{scrollbarWidth: "none", msOverflowStyle: "none"}}
                        >
                            {accountsPages.map((pageAccounts, pageIndex) => (
                                <div key={pageIndex} className="min-w-full shrink-0 snap-start box-border px-1.5">
                                    <div className="grid grid-cols-2 gap-3">
                                        {pageAccounts.map((acc, index) => (
                                            <AccountCard
                                                key={acc.id}
                                                account={acc}
                                                index={pageIndex * pageSize + index}
                                            />
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="sm:hidden mt-1 flex items-center justify-center gap-2">
                            {accountsPages.map((_, pageIndex) => (
                                <button
                                    key={pageIndex}
                                    type="button"
                                    aria-label={`Перейти к странице счетов ${pageIndex + 1}`}
                                    onClick={() => scrollToPage(pageIndex)}
                                    className={`h-2 rounded-full cursor-pointer transition-all hover:bg-brand-on-primary/80 active:scale-95 ${
                                        activePage === pageIndex
                                            ? "w-5 bg-brand-on-primary"
                                            : "w-2 bg-brand-on-primary/40"
                                    }`}
                                />
                            ))}
                        </div>

                        <div
                            className="
                                hidden sm:grid gap-3 mt-2
                                sm:grid-cols-4
                            "
                        >
                            {accountsList.map((acc, index) => (
                                <AccountCard key={acc.id} account={acc} index={index}/>
                            ))}
                        </div>
                    </>
                ) : (
                    <div
                        className="
                            grid gap-3 mt-2
                            grid-cols-2
                            sm:grid-cols-4
                        "
                    >
                        {accountsList.map((acc, index) => (
                            <AccountCard key={acc.id} account={acc} index={index}/>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default MainCard;
