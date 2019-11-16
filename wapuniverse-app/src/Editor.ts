import {fetchRezIndex, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {Cell, CellSink} from "sodiumjs";
import {Vec2} from "./Vec2";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";
import {readWorld} from "./wwd";

export class App {
  readonly _editor = new CellSink(EditorInternal.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

export interface Editor {
  readonly selectedObjects: Cell<ReadonlyArray<EdObject>>;

  readonly objects: ReadonlyArray<EdObject>;

  readonly areaSelection: Cell<AreaSelection | null>;

  selectByArea(origin: Vec2, destination: Cell<Vec2>): void;
}

async function fetchWwd() {
  const wwd = await fetch("WORLD.WWD");
  const blob = await wwd.blob();
  const arrayBuffer = await blob.arrayBuffer();
  const header = readWorld(arrayBuffer);
  console.log(`header: ${header}`);
}

export class EditorInternal implements Editor {
  private _selectedObjects = new CellSink<ReadonlyArray<EdObject>>([]);

  readonly selectedObjects = this._selectedObjects as Cell<ReadonlyArray<EdObject>>;

  readonly objects: ReadonlyArray<EdObject>;

  private _areaSelection = new CellSink<AreaSelection | null>(null);

  readonly areaSelection = this._areaSelection as Cell<AreaSelection | null>;

  private constructor(rezIndex: RezIndex, levelResources: LevelResources) {
    this.objects = [
      new EdObject(
        this,
        rezIndex, levelResources, this.areaSelection,
        new Vec2(64, 64),
        "LEVEL1_IMAGES_OFFICER"
      ),
      new EdObject(
        this,
        rezIndex, levelResources, this.areaSelection,
        new Vec2(256, 256),
        "LEVEL1_IMAGES_SKULL"
      )
    ];
  }

  static async create(): Promise<Editor> {
    await fetchWwd();
    const rezIndex = await fetchRezIndex();
    const resources = await LevelResources.load(rezIndex, 1);
    return new EditorInternal(rezIndex, resources);
  }

  selectByArea(origin: Vec2, destination: Cell<Vec2>): void {
    const areaSelection = new AreaSelection(
      this,
      origin,
      destination,
      this.objects,
      () => {
        this._areaSelection.send(null);
      });
    this._areaSelection.send(areaSelection);
  }

  selectObjects(objects: ReadonlyArray<EdObject>) {
    this._selectedObjects.send(objects);
  }
}
