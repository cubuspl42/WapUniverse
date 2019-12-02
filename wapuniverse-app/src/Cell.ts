import {Stream} from "sodiumjs";
import {useEffect, useMemo, useState} from "react";

type Listener<T> = (value: T) => void;

export abstract class Cell<T> {
  private readonly listeners: Listener<T>[] = [];

  get refCount(): number {
    return this.listeners.length;
  }

  protected onFirstListenerSubscribed(): void {
  }

  protected onLastListenerUnsubscribed(): void {
  }

  protected emit(value: T): void {
    this.listeners.forEach((listener) => {
      listener(value);
    });
  }

  listen(listener: Listener<T>): () => void {
    this.listeners.push(listener);
    if (this.listeners.length == 1) this.onFirstListenerSubscribed();

    const unsubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
      if (this.listeners.length == 0) this.onLastListenerUnsubscribed();
    };

    return unsubscribe;
  }

  abstract sample(): T;

  forEach<R>(f: (value: T) => R): () => void {
    f(this.sample());
    return this.listen(f);
  }

  map<R>(f: (value: T) => R): Cell<R> {
    return new MappedCell(this, f);
  }

  flatMap<R>(f: (value: T) => Cell<R>): Cell<R> {
    return new FlatMappedCell(this, f);
  }

  lift<T1, R>(cell1: Cell<T1>, f: (a: T, b: T1) => R): Cell<R> {
    return new LiftedCell(this, cell1, f);
  }

  static switchC<T>(cell: Cell<Cell<T>>): Cell<T> {
    return cell.flatMap((a) => a);
  }
}

class MappedCell<T, R> extends Cell<R> {
  private readonly source: Cell<T>;
  private readonly f: (value: T) => R;
  private unsubscribe?: () => void;
  private value: R;

  constructor(source: Cell<T>, f: (value: T) => R) {
    super();
    this.source = source;
    this.f = f;
    this.value = f(source.sample());
  }

  sample(): R {
    return this.value;
  }

  protected onFirstListenerSubscribed(): void {
    this.unsubscribe = this.source.listen((value => {
      this.value = this.f(value);
      this.emit(this.value);
    }));
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribe!();
  }
}

class FlatMappedCell<T, R> extends Cell<R> {
  private readonly source: Cell<T>;
  private readonly f: (value: T) => Cell<R>;
  private unsubscribe?: () => void;
  private nested: Cell<R>;
  private unsubscribeNested?: () => void;

  constructor(source: Cell<T>, f: (value: T) => Cell<R>) {
    super();
    this.source = source;
    this.f = f;

    const nested = this.f(source.sample());
    this.nested = nested;
  }

  sample(): R {
    return this.nested.sample(); // ?
  }

  protected onFirstListenerSubscribed(): void {
    const listenToNested = (nested: Cell<R>) => {
      this.unsubscribeNested = nested.listen((value) => this.emit(value));
      this.nested = nested;
    };

    this.unsubscribe = this.source.listen((sourceValue: T) => {
      this.unsubscribeNested!();
      const nested = this.f(sourceValue);
      this.emit(nested.sample());
      listenToNested(nested);
    });

    listenToNested(this.nested);
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribeNested!();
    this.unsubscribe!();
  }
}

class LiftedCell<T1, T2, R> extends Cell<R> {
  private readonly source1: Cell<T1>;
  private readonly source2: Cell<T2>;

  private readonly f: (value1: T1, value2: T2) => R;
  private unsubscribe1?: () => void;
  private unsubscribe2?: () => void;

  private value: R;

  constructor(
    source1: Cell<T1>,
    source2: Cell<T2>,
    f: (value1: T1, value2: T2) => R
  ) {
    super();
    this.source1 = source1;
    this.source2 = source2;
    this.f = f;
    this.value = f(source1.sample(), source2.sample());
  }

  sample(): R {
    return this.value;
  }

  protected onFirstListenerSubscribed(): void {
    this.unsubscribe1 = this.source1.listen((value1 => {
      this.value = this.f(value1, this.source2.sample());
      this.emit(this.value);
    }));

    this.unsubscribe2 = this.source2.listen((value2 => {
      this.value = this.f(this.source1.sample(), value2);
      this.emit(this.value);
    }));
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribe1!();
    this.unsubscribe2!();
  }
}

export class CellSink<T> extends Cell<T> {
  private readonly stream?: Stream<T>;
  private value: T;
  private unsubscribe?: () => void;

  constructor(initialValue: T, stream?: Stream<T>) {
    super();
    this.stream = stream;
    this.value = initialValue;
  }

  sample(): T {
    return this.value;
  }

  send(value: T) {
    this.value = value;
    this.emit(value);
  }

  protected onFirstListenerSubscribed(): void {
    if (this.stream !== undefined) {
      this.unsubscribe = this.stream.listen((newValue => {
        this.value = newValue;
        this.emit(newValue);
      }));
    }
  }

  protected onLastListenerUnsubscribed(): void {
    if (this.unsubscribe !== undefined) {
      this.unsubscribe!();
    }
  }
}

type CellProvider<T> = () => Cell<T>;

export function useCell<T>(cell: Cell<T> | CellProvider<T>): T {
  const cell_ = useMemo<Cell<T>>(
    cell instanceof Cell ? () => {
      return cell;
    } : cell, []);

  const [value, setValue] = useState(cell_.sample());

  useEffect(() => {
    return cell_.listen(setValue);
  }, []);

  return value;
}
