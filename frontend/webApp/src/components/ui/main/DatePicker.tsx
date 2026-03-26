import { IoCalendarOutline } from "react-icons/io5";

interface DatePickerProps {
    value: Date;
    onChange: (date: Date) => void;
}

const DatePicker = ({ value, onChange }: DatePickerProps) => {

    const formatDate = (date: Date) => {
        const months = [
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        ];

        return `${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()}`;
    };

    return (
        <div>
            <div className="relative">
                <input
                    type="date"
                    value={value.toISOString().split('T')[0]}
                    onChange={(e) => onChange(new Date(e.target.value))}
                    className="absolute inset-0 opacity-0 cursor-pointer z-10"
                />

                <div className="flex items-center justify-between px-4 py-3.5 bg-white rounded-2xl border border-gray-200 pointer-events-none">
                    <span className="text-gray-900">
                        {formatDate(value)}
                    </span>
                    <IoCalendarOutline className="w-5 h-5 text-gray-400" />
                </div>
            </div>
        </div>
    );
};

export default DatePicker;