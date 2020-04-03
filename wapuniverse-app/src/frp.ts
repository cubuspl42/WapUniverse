import { Cell, CellSink, Stream } from "sodium";
export { Set } from "./frp/Set";
export { Map } from "./frp/Map";
// declare module "sodiumjs" {
//   export interface Cell<A> {
//     flatMap<R>(f: (value: A) => Cell<R>): Cell<R>;
//     forEach<R>(f: (value: A) => R): () => void;
//   }
// }
//
// Cell.prototype.flatMap = function <A, R>(f: (value: A) => Cell<R>) {
//   return Cell.switchC(this.map(f));
// };
//
// Cell.prototype.forEach = function <A, R>(f: (value: A) => R): () => void {
//   f(this.sample());
//   return this.listen(f);
// };

export class LateCellLoop<T> {
  private readonly cellSink: CellSink<Cell<T>>;

  get cell(): Cell<T> {
    const c = Cell.switchC(this.cellSink);
    c.listen(() => { });
    return c;
  }

  constructor(initValue: T) {
    const sink = new CellSink(new Cell(initValue));
    this.cellSink = sink;
  }

  lateLoop(cell: Cell<T>) {
    this.cellSink.send(cell);
  }
}

export class LateStreamLoop<T> {
  private readonly sink: CellSink<Stream<T>>;

  get stream(): Stream<T> {
    const c = Cell.switchS(this.sink);
    c.listen(() => { });
    return c;
  }

  constructor() {
    const sink = new CellSink(new Stream<T>());
    this.sink = sink;
    sink.listen(() => {});
  }

  lateLoop(stream: Stream<T>) {
    this.sink.send(stream);
    stream.listen(() => {});
  }
}
