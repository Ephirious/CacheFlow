import { useState } from "react";
import { IoChevronDown } from "react-icons/io5";
import {FiArrowRight} from "react-icons/fi";
import {Account} from "k2ts";

interface TransferCardProps {
    accounts: readonly Account[];
    fromId: string | null;
    toId: string | null;
    onSelectFrom: (id: string) => void;
    onSelectTo: (id: string) => void;
}

const AccountDropdown = ({
                             accounts,
                             selectedId,
                             onSelect,
                             label,
                         }: {
    accounts: readonly Account[];
    selectedId: string | null;
    onSelect: (id: string) => void;
    excludeId?: string | null;
    label: string;
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const selectedAccount = accounts.find(acc => acc.id === selectedId);

    return (
        <div className="relative flex-1">
            <label className="block text-sm mb-2 font-medium">
                {label}
            </label>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between px-4 py-3.5 bg-surface-muted rounded-2xl border border-border-strong shadow-sm hover:border-border-default transition-colors"
            >
                <span className={selectedAccount ? "text-text-primary font-medium overflow-hidden h-6" : "text-text-primary"}>
                    {selectedAccount ? selectedAccount.title : "Счёт"}
                </span>
                <IoChevronDown
                    className={`w-5 h-5 text-text-muted transition-transform ${
                        isOpen ? "rotate-180" : ""
                    }`}
                />
            </button>

            {isOpen && (
                <>
                    <div
                        className="fixed inset-0 z-40"
                        onClick={() => setIsOpen(false)}
                    />
                    <div className="absolute z-50 w-full mt-2 bg-surface-base rounded-2xl border border-border-strong shadow-lg overflow-hidden max-h-60 overflow-y-auto">
                        {accounts
                            .map((account) => (
                                <button
                                    key={account.id}
                                    onClick={() => {
                                        onSelect(account.id);
                                        setIsOpen(false);
                                    }}
                                    className="w-full flex items-center gap-3 px-4 py-3 hover:bg-surface-hover transition-colors text-left"
                                >
                                    <div
                                        className="w-3 h-3 rounded-full shrink-0"
                                        style={{ backgroundColor: account.color.normalizedHex }}
                                    />
                                    <div className="flex-1 min-w-0">
                                        <div className="text-text-primary font-medium truncate overflow-hidden">
                                            {account.title}
                                        </div>
                                        <div className="text-text-secondary text-sm">
                                            {account.balance.prettyString()}
                                        </div>
                                    </div>
                                    {selectedId === account.id && (
                                        <div className="w-5 h-5 rounded-full bg-brand-primary flex items-center justify-center shrink-0">
                                            <svg className="w-3 h-3 text-brand-on-primary" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd"
                                                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                                      clipRule="evenodd" />
                                            </svg>
                                        </div>
                                    )}
                                </button>
                            ))}
                    </div>
                </>
            )}
        </div>
    );
};

const TransferCard = ({
                          accounts,
                          fromId,
                          toId,
                          onSelectFrom,
                          onSelectTo,
                      }: TransferCardProps) => {
    return (
        <div className="rounded-2xl">
            <div className="flex items-end gap-3 text-text-primary">
                <AccountDropdown
                    accounts={accounts}
                    selectedId={fromId}
                    onSelect={onSelectFrom}
                    excludeId={toId}
                    label="Откуда"
                />

                <div className="pb-5 text-text-primary">
                    <FiArrowRight className="w-6 h-6" />
                </div>

                <AccountDropdown
                    accounts={accounts}
                    selectedId={toId}
                    onSelect={onSelectTo}
                    excludeId={fromId}
                    label="Куда"
                />
            </div>
        </div>
    );
};

export default TransferCard;
