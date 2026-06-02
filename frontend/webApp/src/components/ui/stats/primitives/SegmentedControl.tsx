import {motion} from "framer-motion";
import {useId} from "react";

interface SegmentedControlOption<T extends string> {
    label: string;
    value: T;
}

interface SegmentedControlProps<T extends string> {
    value: T;
    options: ReadonlyArray<SegmentedControlOption<T>>;
    onChange: (value: T) => void;
}

const SegmentedControl = <T extends string>({
    value,
    options,
    onChange,
}: SegmentedControlProps<T>) => {
    const layoutId = useId();

    return (
        <div className="inline-flex rounded-xl bg-surface-muted p-1 gap-0.5">
            {options.map((option) => {
                const isActive = option.value === value;
                return (
                    <button
                        className={`relative rounded-lg px-4 py-2 font-medium sm:px-3 sm:py-1.5 text-sm cursor-pointer transition-colors hover:text-text-primary
                        ${isActive ? "text-text-primary" : "text-text-secondary"}`}
                        key={option.value}
                        onClick={() => onChange(option.value)}
                        type="button"
                    >
                        {isActive && (
                            <motion.div
                                className="absolute inset-0 rounded-lg bg-surface-base shadow-sm"
                                layoutId={layoutId}
                                transition={{type: "spring", duration: 0.35}}
                            />
                        )}
                        <span className="relative z-10">{option.label}</span>
                    </button>
                );
            })}
        </div>
    );
};

export default SegmentedControl;
