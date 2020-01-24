import * as sd from "sodiumjs";

type Listener<T> = (value: T) => void;

abstract class _Cell<T> {
  private readonly listeners = new Set<Listener<T>>();

  get refCount(): number {
    return this.listeners.size;
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
    this.listeners.add(listener);
    if (this.listeners.size == 1) this.onFirstListenerSubscribed();

    const unsubscribe = () => {
      this.listeners.delete(listener);
      if (this.listeners.size == 0) this.onLastListenerUnsubscribed();
    };

    return unsubscribe;
  }

  abstract sample(): T;

  forEach<R>(f: (value: T) => R): () => void {
    f(this.sample());
    return this.listen(f);
  }

  map<R>(f: (value: T) => R): _Cell<R> {
    return new _MappedCell(this, f);
  }

  flatMap<R>(f: (value: T) => _Cell<R>): _Cell<R> {
    return new _FlatMappedCell(this, f);
  }

  lift<T1, R>(cell1: _Cell<T1>, f: (a: T, b: T1) => R): _Cell<R> {
    return new _LiftedCell(this, cell1, f);
  }

  static switchC<T>(cell: _Cell<_Cell<T>>): _Cell<T> {
    return cell.flatMap((a) => a);
  }
}

class _MappedCell<T, R> extends _Cell<R> {
  private readonly source: _Cell<T>;
  private readonly f: (value: T) => R;
  private unsubscribe?: () => void;
  private value: R;

  constructor(source: _Cell<T>, f: (value: T) => R) {
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
      const newValue = this.f(value);
      if (newValue !== this.value) {
        this.value = newValue;
        this.emit(newValue);
      }
    }));
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribe!();
  }
}

class _FlatMappedCell<T, R> extends _Cell<R> {
  private readonly source: _Cell<T>;
  private readonly f: (value: T) => _Cell<R>;
  private unsubscribe?: () => void;
  private nested: _Cell<R>;
  private unsubscribeNested?: () => void;

  constructor(source: _Cell<T>, f: (value: T) => _Cell<R>) {
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
    const listenToNested = (nested: _Cell<R>) => {
      this.unsubscribeNested = nested.listen((value) => {
        this.emit(value);
      });
      this.nested = nested;
    };

    this.unsubscribe = this.source.listen((sourceValue: T) => {
      this.unsubscribeNested!();
      const newNested = this.f(sourceValue);
      const newValue = newNested.sample();
      if (newValue !== this.nested.sample()) {
        this.emit(newNested.sample());
      }
      listenToNested(newNested);
    });

    listenToNested(this.nested);
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribeNested!();
    this.unsubscribe!();
  }
}

class _LiftedCell<T1, T2, R> extends _Cell<R> {
  private readonly source1: _Cell<T1>;
  private readonly source2: _Cell<T2>;

  private readonly f: (value1: T1, value2: T2) => R;
  private unsubscribe1?: () => void;
  private unsubscribe2?: () => void;

  private value: R;

  constructor(
    source1: _Cell<T1>,
    source2: _Cell<T2>,
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
      const newValue = this.f(value1, this.source2.sample());
      if (newValue !== this.value) {
        this.value = newValue;
        this.emit(newValue);
      }
    }));

    this.unsubscribe2 = this.source2.listen((value2 => {
      const newValue = this.f(this.source1.sample(), value2);
      if (newValue !== this.value) {
        this.value = newValue;
        this.emit(newValue);
      }
    }));
  }

  protected onLastListenerUnsubscribed(): void {
    this.unsubscribe1!();
    this.unsubscribe2!();
  }
}

class _CellSink<T> extends _Cell<T> {
  private readonly stream?: sd.Stream<T>;
  private value: T;
  private unsubscribe?: () => void;

  constructor(initialValue: T, stream?: sd.Stream<T>) {
    super();
    this.stream = stream;
    this.value = initialValue;
  }

  sample(): T {
    return this.value;
  }

  send(value: T) {
    if (value !== this.value) {
      this.value = value;
      this.emit(value);
    }
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

// export {Cell, CellSink} from "sodiumjs";
