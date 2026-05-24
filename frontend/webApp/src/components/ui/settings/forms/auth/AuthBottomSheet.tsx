import {
    AuthChild,
    AuthComponent,
    AuthOutput
} from "k2ts";
import {useValue, when} from "interop";
import RegistrationForm from "./RegistrationForm.tsx";
import LoginForm from "./LoginForm.tsx";

interface AuthBottomSheetProps {
    component: AuthComponent
}

const AuthBottomSheet = ({component}: AuthBottomSheetProps) => {

    const pages = useValue(component.childPages)
    const activeChild = pages.active;

    const isRegistrationActive = activeChild instanceof AuthChild.RegistrationChild;


    return <div style={{backgroundColor: "lightgray", flexDirection: "column", display: "flex", padding: "1rem"}}>
        <>
            {
                when(activeChild)
                    .on(AuthChild.RegistrationChild, (child) => (
                        <RegistrationForm component={child.component}/>
                    ))
                    .on(AuthChild.LoginChild, (child) => (
                        <LoginForm component={child.component}/>
                    ))
                    .run()
            }
        </>


        <button style={{backgroundColor: isRegistrationActive ? "pink" : "white"}} onClick={() => {
            component.onOutput(AuthOutput.NavigateToRegistration)
        }}>
            Регистрация
        </button>
        <button style={{backgroundColor: !isRegistrationActive ? "pink" : "white"}} onClick={() => {
            component.onOutput(AuthOutput.NavigateToLogin)
        }}>
            Вход
        </button>
    </div>
};

export default AuthBottomSheet;
