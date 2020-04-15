// import {Stream} from "./Stream";
import * as frp from "frp";
import {Cell, Stream} from "../sodium";
import {Maybe} from "../Maybe";

export function remove<T>(array: readonly T[], element: T) {
  return array.filter(e => e !== element)
}

// export interface SetChange<T> {
// }

// type SetListener<T> = (change: SetChange<T>) => void;

// type UnlistenFunction = () => void;

export class SetChange<T> {
  readonly remove: ReadonlySet<T>;
  readonly add: ReadonlySet<T>;

  constructor(
    add: ReadonlySet<T>,
    remove: ReadonlySet<T>,
  ) {
    this.add = add;
    this.remove = remove;
  }

  static remove<T>(elements: ReadonlySet<T>) {
    return new SetChange(new Set<T>(), elements);
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

class SetUtils {
  static map<A, B>(s: ReadonlySet<A>, f: (a: A) => B): ReadonlySet<B> {
    const newSet = new Set<B>();
    s.forEach((a) => newSet.add(f(a)));
    return newSet;
  }

  static mapA<A, B>(s: ReadonlySet<A>, f: (a: A) => B): ReadonlyArray<B> {
    const newArray: B[] = [];
    s.forEach((a) => newArray.push(f(a)));
    return newArray;
  }

  static union<A>(s1: ReadonlySet<A>, s2: ReadonlySet<A>): ReadonlySet<A> {
    const newSet = new Set<A>();
    s1.forEach((e) => newSet.add(e));
    s2.forEach((e) => newSet.add(e));
    return newSet;
  }

  static unionA<A>(sets: readonly ReadonlySet<A>[]): ReadonlySet<A> {
    const newSet = new Set<A>();
    sets.forEach((s) => {
      s.forEach((a) => {
        newSet.add(a);
      });
    });
    return newSet;
  }
}

class FrpSet<A> {
  constructor(cell: Cell<ReadonlySet<A>>) {
    this.cell = cell;
  }

  readonly cell: Cell<ReadonlySet<A>>;

  static hold<A>(
    initial: ReadonlySet<A>,
    changes?: Stream<SetChange<A>>,
  ): FrpSet<A> {
    const changes_ = changes ?? new Stream<SetChange<A>>();
    const cell = changes_.accum(initial, (change, set) => {
      const newSet = new Set(set);
      change.remove.forEach((a) => newSet.delete(a));
      change.add.forEach((a) => newSet.add(a));
      return newSet;
    });
    return new FrpSet(cell);
  }

  static flatten<T>(cell: FrpSet<FrpSet<T>>): FrpSet<T> {
    throw new Error("Unimplemented");
  }

  static flattenCJ<T>(cell: Cell<ReadonlySet<T>>): FrpSet<T> {
    return new FrpSet(cell);
  }

  static flattenC<T>(cell: Cell<FrpSet<T>>): FrpSet<T> {
    throw new Error("Unimplemented");
  }

  static unionAll<T>(sets: FrpSet<T>[]): FrpSet<T> {
    throw new Error("Unimplemented");
  }

  flatMap<B>(f: (a: A) => FrpSet<B>): FrpSet<B> {
    const cell: Cell<ReadonlySet<B>> =
      Cell.switchC(this.cell.map((set) => {
        const cellArray: ReadonlyArray<Cell<ReadonlySet<B>>> = SetUtils.mapA(set, (a) => f(a).cell);
        const cSets: Cell<ReadonlyArray<ReadonlySet<B>>> = Cell.liftArray(cellArray);
        const cMergedSet: Cell<ReadonlySet<B>> = cSets.map((sets) => SetUtils.unionA(sets));
        return cMergedSet;
      }));
    return new FrpSet(cell);
  }

  flatMapC<B>(f: (a: A) => Cell<B>): FrpSet<B> {
    throw new Error("Unimplemented");
  }

  sample(): ReadonlySet<A> {
    return this.cell.sample();
  }

  filterC(f: (e: A) => Cell<boolean>): FrpSet<A> {
    const cell = this.cell.flatMap((set) => {
      const array = Array.from(set).map((a) =>
        f(a).map((b) => ({a: a, filtered: b})),
      );
      const cEntryArray = Cell.liftArray(array);
      const cArray: Cell<A[]> = cEntryArray.map((arr) =>
        arr.filter((e) => e.filtered)
          .map((e) => e.a),
      );
      return cArray.map((arr) => new Set(arr));
    });
    return new FrpSet(cell);
  }

  map<B>(f: (a: A) => B): FrpSet<B> {
    const cell = this.cell.map((set) => SetUtils.map(set, f));
    return new FrpSet(cell);
  }

  groupBy<B>(f: (a: A) => B): frp.Map<B, frp.Set<A>> {
    throw new Error("Unimplemented");
  }

  groupByC<B>(f: (a: A) => Cell<B>): frp.Map<B, A> {
    throw new Error("Unimplemented");
  }

  static filterSome<A>(set: FrpSet<Maybe<A>>): FrpSet<A> {
    throw new Error("Unimplemented");
  }

  has(element: A): Cell<boolean> {
    return this.cell.map((set) => set.has(element));
  }

  union(other: FrpSet<A>): FrpSet<A> {
    return FrpSet.flattenCJ(this.cell.lift(other.cell,
      (s1, s2) => {
        console.log(`FrpSet.union lift callback`);
        return SetUtils.union(s1, s2);
      }),
    );
  }
}

export {
  FrpSet as Set
}
