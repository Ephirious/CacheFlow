import './Greeting.css';

import {useState} from 'react';
import {JSLogo} from '../JSLogo/JSLogo.tsx';

import type {AnimationEvent} from 'react';

export function Greeting() {


    // const {root, greeting} = useMemo(() => {
    //     return {
    //         root: initRealRootComponent(),
    //         greeting: new KotlinGreeting()
    //     };
    // }, []);

    const [isVisible, setIsVisible] = useState<boolean>(false);
    const [isAnimating, setIsAnimating] = useState<boolean>(false);
    // const [activeChild, setActiveChild] = useState(root.activeChild);

    // useEffect(() => {
    //     const disposable = root.observeJsStack((stack) => {
    //         setActiveChild(stack.active);
    //     });
    //     return () => disposable.dispose();
    // }, [root]);


    // const renderContent = () => {
    //     if (activeChild instanceof RootChild.InteropTestChild) {
    //         const component = activeChild.component;
            //
            // return <div>Interop Test Component Active</div>;
        // }
        // return null;
    // };

    const handleClick = () => {
        if (isVisible) {
            setIsAnimating(true);
        } else {
            setIsVisible(true);
        }
    };

    const handleAnimationEnd = (event: AnimationEvent<HTMLDivElement>) => {
        if (event.animationName === 'fadeOut') {
            setIsVisible(false);
            setIsAnimating(false);
        }
    };

    return (
        <div className="greeting-container">
            <button onClick={handleClick} className="greeting-button">
                Click me!
            </button>

            {isVisible && (
                <div className={isAnimating ? 'greeting-content fade-out' : 'greeting-content'}
                     onAnimationEnd={handleAnimationEnd}>
                    <JSLogo/>
                    {/*<div>React: {greeting.greet()}</div>*/}
                    {/*{renderContent()}*/}
                </div>
            )}
        </div>
    );
}