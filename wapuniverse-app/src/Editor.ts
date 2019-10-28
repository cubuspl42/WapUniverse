import {fetchRezIndex, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {Cell, CellSink} from "sodiumjs";
import {Vec2} from "./Vec2";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";

export class App {
  editor = Editor.create();
}

export class Editor {
  readonly objects: ReadonlyArray<EdObject>;

  private _areaSelection = new CellSink<AreaSelection | null>(null);

  readonly areaSelection = this._areaSelection as Cell<AreaSelection | null>;

  private constructor(rezIndex: RezIndex, levelResources: LevelResources) {
    this.objects = [
      new EdObject(
        rezIndex, levelResources, this.areaSelection,
        new Vec2(64, 64),
        "LEVEL1_IMAGES_OFFICER"
      ),
      new EdObject(
        rezIndex, levelResources, this.areaSelection,
        new Vec2(256, 256),
        "LEVEL1_IMAGES_SKULL"
      )
    ];
  }

  static async create(): Promise<Editor> {
    const rezIndex = await fetchRezIndex();
    const resources = await LevelResources.load(rezIndex, 1);
    return new Editor(rezIndex, resources);
  }

  selectByArea(origin: Vec2, destination: Cell<Vec2>) {
    const areaSelection = new AreaSelection(origin, destination, this.objects, () => {
      this._areaSelection.send(null);
    });
    this._areaSelection.send(areaSelection);
  }
}
