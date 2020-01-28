import {Cell, CellSink, StreamSink, Stream} from "sodiumjs";

declare module "sodiumjs" {
  export interface Cell<A> {
    flatMap<R>(f: (value: A) => Cell<R>): Cell<R>;

    forEach<R>(f: (value: A) => R): () => void;
  }
}

Cell.prototype.flatMap = function <A, R>(f: (value: A) => Cell<R>) {
  return Cell.switchC(this.map(f));
};

Cell.prototype.forEach = function <A, R>(f: (value: A) => R): () => void {
  f(this.sample());
  return this.listen(f);
};

export function eventStream<K extends keyof HTMLElementEventMap>(element: HTMLElement, type: K): Stream<HTMLElementEventMap[K]> {
  const stream = new StreamSink<HTMLElementEventMap[K]>();
  element.addEventListener(type, (e) => stream.send(e)); // TODO: Cleanup
  return stream;
}

export {Cell, CellSink};
