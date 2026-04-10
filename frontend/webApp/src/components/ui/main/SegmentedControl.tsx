import {motion} from "framer-motion";

interface SegmentedControlProps {
    value: string;
    onChange: (value: string) => void;
}

const SegmentedControl = ({value, onChange}: SegmentedControlProps) => {
    const options = [
        {id: "Income", label: "Доход"},
        {id: "Outcome", label: "Расход"},
        {id: "Transfer", label: "Перевод"},
    ];

    return (
        <div className="flex bg-[#F3F4F6] p-1 rounded-2xl gap-2">
            {options.map((option) => (
                <button
                    key={option.id.toString()}
                    onClick={() => onChange(option.id)}
                    className="relative flex-1 py-2 px-4 text-lg font-medium text-gray-500"
                >
                    {value === option.id && (
                        <motion.div
                            layoutId="activeTab"
                            className="absolute inset-0 bg-white rounded-xl shadow-sm"
                            transition={{type: "spring", duration: 0.5}}
                        />
                    )}
                    <span className="relative z-10">
                        {option.label}
                    </span>
                </button>
            ))}
        </div>
    );
};

export default SegmentedControl;