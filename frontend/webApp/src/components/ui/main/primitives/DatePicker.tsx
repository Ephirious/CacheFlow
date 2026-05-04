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

                <div className="flex items-center justify-between px-4 py-3.5 bg-surface-base rounded-2xl border border-border-strong pointer-events-none">
                    <span className="text-text-primary">
                        {formatDate(value)}
                    </span>
                    <IoCalendarOutline className="w-5 h-5 text-text-muted" />
                </div>
            </div>
        </div>
    );
};

export default DatePicker;
