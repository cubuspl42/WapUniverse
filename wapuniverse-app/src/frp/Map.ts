// import {Stream} from "./Stream";

import {Cell, Stream} from "../sodium";
import {Maybe} from "../Maybe";

type MapListener<K, V> = (change: MapChange<K, V>) => void;

type UnlistenFunction = () => void;

interface MapChange<K, V> {
  readonly remove: Set<K>;
  readonly assign: Map<K, V>;
}

// export class ValueAdded<T> implements MapChange<T> {
//   readonly value: T;
//
//   constructor(value: T) {
//     this.value = value;
//   }
// }
//
// export class ValueRemoved<T> implements MapChange<T> {
//   readonly value: T;
//
//   constructor(value: T) {
//     this.value = value;
//   }
// }

class FrpMap<K, V> {
  constructor(
    initial: Map<K, V>,
    changes: Stream<MapChange<K, V>> = new Stream(),
  ) {
  }

  listen(listener: MapListener<K, V>): UnlistenFunction {
    throw new Error("Unimplemented");
  }

  get(key: K): Cell<Maybe<V>> {
    throw new Error("Unimplemented");
  }

  mapValues<A>(f: (v: V) => A): FrpMap<K, A> {
    throw new Error("Unimplemented");
  }

  sample(): ReadonlyMap<K, V> {
    throw new Error("Unimplemented");
  }
}

export {
  FrpMap as Map
}
