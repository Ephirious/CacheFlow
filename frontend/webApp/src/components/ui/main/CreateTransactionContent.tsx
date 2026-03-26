import SegmentedControl from "./SegmentedControl.tsx";
import {TransactionType} from "../../../types/types.ts";
import {useState} from "react";
import {Category} from "../../../types/types.ts";
import CategorySelector from "./CategorySelector.tsx";
import AccountSelector from "./AccountSelector.tsx";
import DatePicker from "./DatePicker.tsx";
import TextArea from "./TextArea.tsx";

const CATEGORIES: Category[] = [
    { id: "1", name: "Продукты", color: "#EF4444" },
    { id: "2", name: "Транспорт", color: "#F59E0B" },
    { id: "3", name: "Развлечения", color: "#8B5CF6" },
    { id: "4", name: "Кафе", color: "#10B981" },
    { id: "5", name: "Одежда", color: "#3B82F6" },
];

const TEST_ACCOUNTS = [
    { id: "1", title: "Т-Банк", balance: "100 000 ₽", color: "bg-yellow-500" },
    { id: "2", title: "Сбер", balance: "50 000 ₽", color: "bg-green-500" },
    { id: "3", title: "Тинькофф", balance: "75 000 ₽", color: "bg-red-500" },
];

const CreateTransactionContent = () => {
    const [type, setType] = useState<TransactionType>("expense");
    const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
    const [selectedAccount, setSelectedAccount] = useState<string | null>(null);
    const [date, setDate] = useState(new Date());
    const [note, setNote] = useState("");
    return (
        <div className="flex w-full flex-col px-6 py-2 gap-4">
            <span className="flex text-2xl font-bold justify-center">Новая транзакция</span>
            <div className="flex flex-col w-full gap-3">
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Сумма</span>
                    <input
                        type="text"
                        placeholder="0"
                        className="w-full px-4 py-3 bg-white border border-sheet-input rounded-xl"
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Тип</span>
                    <SegmentedControl value={type} onChange={setType}/>
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Категория</span>
                    <CategorySelector
                        categories={CATEGORIES}
                        selectedId={selectedCategory}
                        onSelect={setSelectedCategory}
                        onAdd={() => console.log("Add category")}
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Счёт</span>
                    <AccountSelector
                        accounts={TEST_ACCOUNTS}
                        selectedId={selectedAccount}
                        onSelect={setSelectedAccount}
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Дата</span>
                    <DatePicker
                        value={date}
                        onChange={setDate}
                    />
                </div>
                <div className="flex w-full flex-col gap-2">
                    <span className="text-sm font-medium">Заметка (необязательно)</span>
                    <TextArea
                        value={note}
                        onChange={setNote}
                        placeholder="Добавьте описание..."
                    />
                </div>
                <button className="bg-brand-indigo py-4 text-base font-bold text-white rounded-2xl">Сохранить</button>
            </div>
        </div>
    )
}
export default CreateTransactionContent;