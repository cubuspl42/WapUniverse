// import {Stream} from "./Stream";

export function remove<T>(array: readonly T[], element: T) {
  return array.filter(e => e !== element)
}

// export interface SetChange<T> {
// }

type SetListener<T> = (change: SetChange<T>) => void;

type UnlistenFunction = () => void;

class SetChange<T> {
  readonly add: Set<T>;
  readonly remove: Set<T>;

  constructor(
    add: Set<T>,
    remove: Set<T>,
  ) {
    this.add = add;
    this.remove = remove;
  }
}

// export class ValueAdded<T> implements SetChange<T> {
//   readonly value: T;
//
//   constructor(value: T) {
//     this.value = value;
//   }
// }
//
// export class ValueRemoved<T> implements SetChange<T> {
//   readonly value: T;
//
//   constructor(value: T) {
//     this.value = value;
//   }
// }

class Set_<T> {
  private listeners: SetListener<T>[] = [];

  private readonly set: ReadonlySet<T>;

  constructor(
    initial: Set<T>,
    // changes: Stream<SetChange<T>> = Stream.never(),
  ) {
    this.set = initial;
  }

  listen(listener: SetListener<T>): UnlistenFunction {
    this.listeners.push(listener);
    return () => this.listeners = remove(this.listeners, listener);
  }

  sample(): ReadonlySet<T> {
    return this.set;
  }
}

export {
  Set_ as Set
}
