type ClassType<T> = new (...args: any[]) => T;

class Matcher<T extends object, R = never> {
    private result: R | undefined;
    private matched = false;

    constructor(private value: T) {
    }

    on<V extends T, NextR>(
        type: ClassType<V> | ClassType<V>[],
        handler: (value: V) => NextR
    ): Matcher<T, R | NextR> {
        const self = this as unknown as Matcher<T, R | NextR>;
        if (self.matched) return self;

        // Проверяем: массив это или одиночный класс
        const types = Array.isArray(type) ? type : [type];
        const isMatch = types.some(t => this.value instanceof t);

        if (isMatch) {
            self.result = handler(this.value as V);
            self.matched = true;
        }
        return self;
    }

    is<NextR>(instance: any, handler: () => NextR): Matcher<T, R | NextR> {
        const self = this as unknown as Matcher<T, R | NextR>;
        if (!self.matched && this.value === instance) {
            self.result = handler();
            self.matched = true;
        }
        return self;
    }

    otherwise<NextR>(handler: () => NextR): R | NextR {
        if (this.matched) return this.result as R;
        return handler();
    }

    run(): R | null {
        return this.matched ? (this.result as R) : null;
    }
}

// kotlin naming
export const when = <T extends object>(value: T) => new Matcher<T>(value);