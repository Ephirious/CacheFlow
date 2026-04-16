interface SegmentedControlOption<T extends string> {
    label: string;
    value: T;
}

interface SegmentedControlProps<T extends string> {
    value: T;
    options: ReadonlyArray<SegmentedControlOption<T>>;
    onChange: (value: T) => void;
    compact?: boolean;
}

const SegmentedControl = <T extends string>({
    value,
    options,
    onChange,
    compact = false
}: SegmentedControlProps<T>) => {
    return (
        <div className={`inline-flex rounded-xl bg-surface-muted p-0.5 sm:p-1 ${compact ? "gap-1" : ""}`}>
            {options.map((option) => {
                const isActive = option.value === value;
                return (
                    <button
                        className={`rounded-lg px-4 py-2 font-medium transition-colors sm:px-3 sm:py-1.5 ${
                            compact ? "text-xs sm:text-xs" : "text-xs sm:text-sm"
                        } ${isActive ? "bg-surface-base text-text-primary shadow-sm" : "text-text-secondary"}`}
                        key={option.value}
                        onClick={() => onChange(option.value)}
                        type="button"
                    >
                        {option.label}
                    </button>
                );
            })}
        </div>
    );
};

export default SegmentedControl;
