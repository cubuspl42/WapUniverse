import {Cell} from "sodiumjs";
import {Rectangle} from "./Rectangle";
import {EdObject} from "./EdObject";
import {Vec2} from "./Vec2";

export class AreaSelection {
  readonly rectangle: Cell<Rectangle>;

  readonly objectsInArea: Cell<ReadonlyArray<EdObject>>;

  private readonly _onDone: () => void;

  constructor(
    origin: Vec2,
    destination: Cell<Vec2>,
    objects: ReadonlyArray<EdObject>, onDone: () => void
  ) {
    this._onDone = onDone;
    const area = destination.map(d => {
      return new Rectangle(
        origin.x, origin.y, d.x - origin.x, d.y - origin.y);
    });
    this.rectangle = area;
    this.objectsInArea = area.map((area) => {
      return objects.filter((object) => {
        const boundingBox = object.boundingBox.sample();
        return area.overlaps(boundingBox);
      }) as ReadonlyArray<EdObject>;
    });
  }

  commit() {
    this._onDone();
  }
}
