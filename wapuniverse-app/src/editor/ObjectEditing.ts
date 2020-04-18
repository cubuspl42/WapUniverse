import {EdObject} from "./EdObject";
import {Cell, StreamLoop, Unit} from "sodium";
import {Vec2} from "../Vec2";
import {CellSink, StreamSink} from "sodiumjs";
import {LazyGetter} from "lazy-get-decorator";

export class ObjectEditing {
  readonly object: EdObject;

  @LazyGetter()
  get position(): CellSink<Vec2> {
    return new CellSink<Vec2>(this.object.position.sample());
  }

  setX(x: number) {
    this.position.send(new Vec2(x, this.position.sample().y));
  }

  setY(y: number) {
    this.position.send(new Vec2(this.position.sample().x, y));
  }

  readonly onEnd = new StreamSink<Unit>();

  doEnd(): void {
    this.onEnd.send(Unit.UNIT);
  }

  constructor(object: EdObject) {
    this.object = object;
  }
}
