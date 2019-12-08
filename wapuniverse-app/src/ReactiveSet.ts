import {Cell, CellSink} from "./Cell";

export interface ReactiveReadonlySet<T> {
  size: Cell<number>

  has(x: T): Cell<boolean>

  has(x: Cell<T>): Cell<boolean>
}

class Node<T> {
  value: T;

  constructor(initialValue: T) {
    this.value = initialValue;
  }
}

export class ReactiveSet<T> implements ReactiveReadonlySet<T> {
  private _set = new Set<T>();

  private _size = new CellSink(0);

  readonly size = this._size as Cell<number>;

  view(): ReadonlySet<T> {
    return this._set;
  }

  has(element: T): Cell<boolean>;

  has(element: Cell<T>): Cell<boolean>;

  has(element: T | Cell<T>): Cell<boolean> {
    const elementC = element instanceof Cell ?
      element as Cell<T> :
      new CellSink(element) as Cell<T>;
  }

  add(element: T) {
    if (!this._set.has(element)) {
      this._set.add(element);
      this._size.send(this._size.sample() + 1);
    }
  }

}
