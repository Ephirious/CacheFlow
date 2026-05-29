import {Account} from "k2ts";
import {useState} from "react";
import {IoChevronDown} from "react-icons/io5";

interface AccountSelectorProps {
    accounts: readonly Account[];
    selectedId: string | null;
    onSelect: (id: string) => void;
}

const AccountSelector = ({accounts, selectedId, onSelect}: AccountSelectorProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const selectedAccount = accounts.find(acc => acc.id === selectedId);

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between px-4 py-3.5 bg-surface-muted rounded-2xl border border-border-strong cursor-pointer transition-all hover:bg-surface-hover active:scale-[0.98]"
            >
                <span className={selectedAccount ? "text-text-primary truncate overflow-hidden pr-2" : "text-text-primary pr-2"}>
                    {selectedAccount ? selectedAccount.title : "Выберите счёт"}
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
                    <div
                        className="absolute z-50 w-full mt-2 bg-surface-base rounded-2xl border border-border-strong shadow-lg overflow-hidden">
                        {accounts.map((account) => (
                            <button
                                key={account.id}
                                onClick={() => {
                                    onSelect(account.id);
                                    setIsOpen(false);
                                }}
                                className="w-full flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors hover:bg-surface-hover active:bg-surface-hover/80"
                            >
                                <div
                                    className="w-3 h-3 rounded-full shrink-0"
                                    style={{ backgroundColor: account.color.normalizedHex }}
                                />
                                <div className="flex-1 text-left min-w-0">
                                    <div className="text-text-primary font-medium truncate overflow-hidden">
                                        {account.title}
                                    </div>
                                    <div className="text-text-secondary text-sm">
                                        {account.balance.prettyString()}
                                    </div>
                                </div>
                                {selectedId === account.id && (
                                    <div
                                        className="w-5 h-5 rounded-full bg-brand-primary flex items-center justify-center">
                                        <svg className="w-3 h-3 text-brand-on-primary" fill="currentColor" viewBox="0 0 20 20">
                                            <path fillRule="evenodd"
                                                  d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                                  clipRule="evenodd"/>
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

export default AccountSelector;
