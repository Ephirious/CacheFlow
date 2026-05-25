interface TextAreaProps {
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
}

const TextArea = ({
                      value,
                      onChange,
                      placeholder = "Добавьте описание...",
                  }: TextAreaProps) => {
    return (
        <div>
            <textarea
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder}
                rows={4}
                className="w-full px-4 py-3 bg-surface-muted border border-border-strong rounded-2xl
                         text-text-primary placeholder:text-text-muted
                         focus:outline-none focus:ring-2 focus:ring-brand-primary/20
                         focus:border-brand-primary
                         resize-none transition-all"
            />
        </div>
    );
};

export default TextArea;
