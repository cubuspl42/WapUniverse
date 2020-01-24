import {Cell, CellSink} from "sodiumjs";

declare module "sodiumjs" {
  export interface Cell<A> {
    flatMap<R>(f: (value: A) => Cell<R>): Cell<R>;
    forEach<R>(f: (value: A) => R): () => void;
  }
}

Cell.prototype.flatMap = function<A, R>(f: (value: A) => Cell<R>) {
  return Cell.switchC(this.map(f));
};

Cell.prototype.forEach = function<A, R>(f: (value: A) => R): () => void {
  f(this.sample());
  return this.listen(f);
};

export {Cell, CellSink};
