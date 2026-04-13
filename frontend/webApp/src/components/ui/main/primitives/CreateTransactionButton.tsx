import { IoAdd } from "react-icons/io5";

interface CreateTransactionButtonProps {
    onClick?: () => void;
}

const CreateTransactionButton = ({onClick}: CreateTransactionButtonProps) => {
    return (
        <button
            onClick={onClick}
            className="fixed z-60 p-3 w-14 h-14 bottom-32 right-5 bg-brand-indigo rounded-2xl active:scale-95 transition-transform"
        >
            <IoAdd className="w-full h-full stroke-white"/>
        </button>
    )
}

export default CreateTransactionButton;