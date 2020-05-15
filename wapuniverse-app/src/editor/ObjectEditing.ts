import {EdObject} from "./EdObject";
import {Cell, StreamLoop, Unit} from "sodium";
import {Vec2} from "../Vec2";
import {CellSink, StreamSink} from "sodiumjs";
import {LazyGetter} from "lazy-get-decorator";

export class ObjectEditing {
  readonly object: EdObject;

  readonly position: CellSink<Vec2>;

  get x(): Cell<number> {
    return this.position.map((p) => p.x);
  }

  get y(): Cell<number> {
    return this.position.map((p) => p.y);
  }

  setX(x: number) {
    this.position.send(new Vec2(x, this.position.sample().y));
  }

  setY(y: number) {
    this.position.send(new Vec2(this.position.sample().x, y));
  }
  readonly z: CellSink<number>;

  readonly i: CellSink<number>;

  readonly onEnd = new StreamSink<Unit>();

  doEnd(): void {
    this.onEnd.send(Unit.UNIT);
  }

  constructor(object: EdObject) {
    this.object = object;
    this.position = new CellSink(this.object.position.sample());
    this.z = new CellSink(this.object.z.sample());
    this.i = new CellSink(this.object.i.sample());
  }
}
