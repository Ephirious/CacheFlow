export interface Category {
    id: string;
    name: string;
    color: string;
}

interface BaseTransaction {
    id: number;
    value: number;
    title: string;
    date: string;
    accountName: string;
}

export interface Income extends BaseTransaction { type: 'Income'; category: Category; }
export interface Outcome extends BaseTransaction { type: 'Outcome'; category: Category; }
export interface Transfer extends BaseTransaction { type: 'Transfer'; from: string; to: string; }

export type Transaction = Income | Outcome | Transfer;


export interface Account {
    title: string;
    balance: string;
    color: string;
}
export type TransactionType = "income" | "outcome" | "transfer";

