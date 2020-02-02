import { Rectangle } from "./Rectangle";
import { EdObject } from "./EdObject";
import { Vec2 } from "./Vec2";
import { EditorInternal } from "./Editor";
import { Cell } from "./frp";

export class AreaSelection {
  private readonly _editor: EditorInternal;

  readonly rectangle: Cell<Rectangle>;

  readonly objectsInArea: Cell<ReadonlySet<EdObject>>;

  private readonly _onDone: () => void;

  constructor(
    editor: EditorInternal,
    origin: Vec2,
    destination: Cell<Vec2>,
    objects: ReadonlyArray<EdObject>, onDone: () => void
  ) {
    this._editor = editor;
    this._onDone = onDone;
    const area = destination.map(d => {
      return new Rectangle(origin, d.sub(origin));
    });
    this.rectangle = area;
    const objectsInArea = area.map((area) => {
      return new Set(objects.filter((object) => {
        const boundingBox = object.boundingBox.sample();
        return area.overlaps(boundingBox);
      })) as ReadonlySet<EdObject>;
    });
    objectsInArea.listen(() => { }); // for .sample()
    this.objectsInArea = objectsInArea;
  }

  commit() {
    const objects = this.objectsInArea.sample();
    this._editor.selectObjects(objects);
    this._onDone();
  }
}
