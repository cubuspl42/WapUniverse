declare module 'flatqueue' {
    declare class FlatQueue<T> {
        constructor();
        push: (id: T, value: number) => void;
        pop: () => T | undefined;
    }

    export = FlatQueue;
}