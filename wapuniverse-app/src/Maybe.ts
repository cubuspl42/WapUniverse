export abstract class Maybe<T> {
  abstract get(): T;

  abstract map<R>(f: (value: T) => R): Maybe<R>;

  abstract flatMap<R>(f: (value: T) => Maybe<R>): Maybe<R>;

  abstract orElse(value: () => T): T;

  abstract isSome(): boolean;

  abstract filter<R>(f: (value: T) => boolean): Maybe<T>;

  static test<T>(b: Boolean, value: () => T): Maybe<T> {
    return b ? new Some(value()) : new None();
  }

  static map2<T1, T2, R>(
    maybe1: Maybe<T1>, maybe2: Maybe<T2>,
    f: (value1: T1, value2: T2) => R
  ): Maybe<R> {
    return maybe1.flatMap(value1 =>
      maybe2.map(value2 => f(value1, value2)));
  }

  static map3<T1, T2, T3, R>(
    maybe1: Maybe<T1>, maybe2: Maybe<T2>, maybe3: Maybe<T3>,
    f: (value1: T1, value2: T2, value3: T3) => R
  ): Maybe<R> {
    return maybe1.flatMap(value1 =>
      maybe2.flatMap(value2 =>
        maybe3.map(value3 => f(value1, value2, value3))));
  }

  static findSome<T>(array: ReadonlyArray<Maybe<T>>): Maybe<T> {
    const element = array.find((expansion) => expansion.isSome());
    return element || new None();
  }

  static ofUndefined<T>(value: T | undefined): Maybe<T> {
    return Maybe.test(!!value, () => value!);
  }
}


export class Some<T> extends Maybe<T> {
  private readonly value: T;

  constructor(value: T) {
    super();
    this.value = value;
  }

  map<R>(f: (value: T) => R): Some<R> {
    return new Some<R>(f(this.value));
  }

  flatMap<R>(f: (value: T) => Maybe<R>): Maybe<R> {
    return f(this.value);
  }

  orElse(value: () => T): T {
    return this.value;
  }

  isSome(): boolean {
    return true;
  }

  get(): T {
    return this.value;
  }

  filter<R>(f: (value: T) => boolean): Maybe<T> {
    if (f(this.value)) {
      return this;
    } else return new None();
  }
}

export class None<T> implements Maybe<T> {
  map<R>(f: (value: T) => R): None<R> {
    return new None<R>();
  }

  flatMap<R>(f: (value: T) => Maybe<R>): Maybe<R> {
    return new None<R>();
  }

  orElse(value: () => T): T {
    return value();
  }

  isSome(): boolean {
    return false;
  }

  get(): T {
    throw new TypeError("Tried to call get() on None");
  }

  filter<R>(f: (value: T) => boolean): Maybe<T> {
    return this;
  }
}
