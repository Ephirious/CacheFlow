import { useState } from "react";
import { IoChevronDown } from "react-icons/io5";

interface Account {
    id: string;
    title: string;
    balance: string;
    color: string;
}

interface AccountSelectorProps {
    accounts: Account[];
    selectedId: string | null;
    onSelect: (id: string) => void;
}

const AccountSelector = ({ accounts, selectedId, onSelect }: AccountSelectorProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const selectedAccount = accounts.find(acc => acc.id === selectedId);

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between px-4 py-3.5 bg-white rounded-2xl border border-gray-200"
            >
                <span className={selectedAccount ? "text-gray-900" : "text-gray-500"}>
                    {selectedAccount ? selectedAccount.title : "Выберите счёт"}
                </span>
                <IoChevronDown
                    className={`w-5 h-5 text-gray-400 transition-transform ${
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
                    <div className="absolute z-50 w-full mt-2 bg-white rounded-2xl border border-gray-200 shadow-lg overflow-hidden">
                        {accounts.map((account) => (
                            <button
                                key={account.id}
                                onClick={() => {
                                    onSelect(account.id);
                                    setIsOpen(false);
                                }}
                                className="w-full flex items-center gap-3 px-4 py-3 hover:bg-gray-50 transition-colors"
                            >
                                <div
                                    className={`w-2 h-2 rounded-full ${account.color}`}
                                />
                                <div className="flex-1 text-left">
                                    <div className="text-gray-900 font-medium">
                                        {account.title}
                                    </div>
                                    <div className="text-gray-500 text-sm">
                                        {account.balance}
                                    </div>
                                </div>
                                {selectedId === account.id && (
                                    <div className="w-5 h-5 rounded-full bg-brand-indigo flex items-center justify-center">
                                        <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                                            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
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