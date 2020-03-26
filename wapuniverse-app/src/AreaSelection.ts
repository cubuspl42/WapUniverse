import {Rectangle} from "./Rectangle";
import {EdObject} from "./editor/EdObject";
import {Vec2} from "./Vec2";
import {Cell} from "sodium";

export class AreaSelection {
  readonly rectangle: Cell<Rectangle>;

  readonly objectsInArea: Cell<ReadonlySet<EdObject>>;

  constructor(
    destination: Cell<Vec2>,
    objects: ReadonlyArray<EdObject>,
  ) {
    const origin = destination.sample();
    const area = destination.map(d => new Rectangle(origin, d.sub(origin)));
    const objectsInArea = area.map((area) => {
      return new Set(objects.filter((object) => {
        const boundingBox = object.boundingBox.sample();
        return area.overlaps(boundingBox);
      })) as ReadonlySet<EdObject>;
    });

    this.rectangle = area;
    this.objectsInArea = objectsInArea;
  }
}

