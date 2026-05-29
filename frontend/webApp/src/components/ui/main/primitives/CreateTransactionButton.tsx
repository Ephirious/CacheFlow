import { IoAdd } from "react-icons/io5";

interface CreateTransactionButtonProps {
    onClick?: () => void;
    className?: string;
}

const CreateTransactionButton = ({onClick, className = ""}: CreateTransactionButtonProps) => {
    return (
        <button
            onClick={onClick}
            className={`fixed z-60 h-14 w-14 rounded-2xl bg-brand-primary p-3 shadow-lg cursor-pointer transition-all hover:opacity-90 active:scale-95 ${className}`.trim()}
        >
            <IoAdd className="w-full h-full stroke-brand-on-primary"/>
        </button>
    )
}

export default CreateTransactionButton;
