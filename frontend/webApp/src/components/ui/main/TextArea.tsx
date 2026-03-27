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
                className="w-full px-4 py-3 bg-white border border-gray-200 rounded-2xl
                         text-gray-900 placeholder:text-gray-400
                         focus:outline-none focus:ring-2 focus:ring-brand-indigo/20
                         focus:border-brand-indigo
                         resize-none transition-all"
            />
        </div>
    );
};

export default TextArea;