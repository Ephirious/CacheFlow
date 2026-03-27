import {motion} from "framer-motion";
import {Account} from "k2ts";


const AccountCard = ({account, index}: { account: Account, index: number }) => {
    return (

        <motion.div
            initial={{opacity: 0, y: 20}}
            animate={{opacity: 1, y: 0}}
            transition={{delay: 0.05 * index}}
        >
            <motion.div
                className="
                                    cursor-pointer select-none
                                    flex flex-col gap-2 p-4
                                    bg-white/10 border border-white/10 rounded-2xl
                                    hover:bg-white/15 transition-colors
                                "
                whileTap={{scale: 0.98}}
                whileHover={{scale: 1.02}}
            >
                <div className="flex items-center gap-2">
                    <div className={`w-2 h-2 rounded-full`}
                         style={{backgroundColor: account.color.normalizedHex}}></div>
                    <div className="text-[10px] sm:text-xs font-medium text-white/70 uppercase tracking-wider">
                        {account.title}
                    </div>
                </div>
                <div className="text-lg sm:text-xl font-bold text-white leading-none">
                    {account.balance.prettyString()}
                </div>
            </motion.div>
        </motion.div>
    )
}

export default AccountCard;