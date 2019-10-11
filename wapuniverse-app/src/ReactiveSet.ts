import {Cell, CellSink} from 'sodiumjs';

export interface ReactiveReadonlySet<T> extends ReadonlySet<T> {
  sizeC: Cell<number>

  hasC(x: T): Cell<boolean>

  hasCC(x: Cell<T>): Cell<boolean>
}

export class ReactiveSet<T> implements Set<T>, ReactiveReadonlySet<T> {

  private _set = new Set<T>();

  private _size = new CellSink(0);

  get sizeC(): Cell<number> {
    return this._size;
  }

  hasC(x: T): Cell<boolean> {
    throw new Error("Method not implemented.");
  }

  hasCC(x: Cell<T>): Cell<boolean> {
    throw new Error("Method not implemented.");
  }

  forEach(callback: (value: T, value2: T, set: Set<T>) => void, thisArg?: any): void {
    this._set.forEach(callback, thisArg);
  }

  has(value: T): boolean {
    return this._set.has(value);
  }

  get size(): number {
    return this._set.size;
  }

  [Symbol.iterator](): IterableIterator<T> {
    return this._set[Symbol.iterator]();
  }

  entries(): IterableIterator<[T, T]> {
    throw new Error("Method not implemented.");
  }

  keys(): IterableIterator<T> {
    throw new Error("Method not implemented.");
  }

  values(): IterableIterator<T> {
    throw new Error("Method not implemented.");
  }

}
