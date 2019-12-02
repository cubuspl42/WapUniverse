import {fetchRezIndex, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {Cell, CellSink} from "./Cell";
import {Vec2} from "./Vec2";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";
import {readWorld, World} from "./wwd";
import {Maybe, None, Some} from "./Maybe";

function decode(s: Uint8Array): string {
  return new TextDecoder().decode(s);
}

export class App {
  readonly _editor = new CellSink(EditorInternal.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

export interface Editor {
  readonly selectedObjects: Cell<ReadonlyArray<EdObject>>;

  readonly objects: ReadonlyArray<EdObject>;

  readonly areaSelection: Cell<Maybe<AreaSelection>>;

  startAreaSelection(origin: Vec2, destination: Cell<Vec2>): AreaSelection;
}

async function fetchWwd() {
  const wwd = await fetch("WORLD14.WWD");
  const blob = await wwd.blob();
  const arrayBuffer = await blob.arrayBuffer();
  return readWorld(arrayBuffer);
}

interface PrefixEntry {
  readonly prefix: string;
  readonly expansion: string;
}

export function stopwatch_<R>(s: string, f: () => R) {
  // const a = Date.now();
  const r = f();
  // const b = Date.now();
  // console.log(`${s} - Elapsed: ${b - a}`);
  return r;
}

export function stopwatch<R>(s: string, f: () => R) {
  const a = Date.now();
  const r = f();
  const b = Date.now();
  console.log(`${s} - Elapsed: ${b - a}`);
  return r;
}

export class EditorInternal implements Editor {
  private readonly _areaSelection = new CellSink<Maybe<AreaSelection>>(new None());

  private readonly _selectedObjects = new CellSink<ReadonlyArray<EdObject>>([]);

  readonly objects: ReadonlyArray<EdObject>;

  readonly imageSets: ReadonlyArray<PrefixEntry>;

  readonly areaSelection = this._areaSelection as Cell<Maybe<AreaSelection>>;

  readonly selectedObjects = this._selectedObjects as Cell<ReadonlyArray<EdObject>>;

  private constructor(rezIndex: RezIndex, levelResources: LevelResources, wwd: World) {
    const action = wwd.planes[1];
    this.imageSets = [
      {prefix: decode(wwd.prefix1), expansion: decode(wwd.imageSet1)},
      {prefix: decode(wwd.prefix2), expansion: decode(wwd.imageSet2)},
      {prefix: decode(wwd.prefix3), expansion: decode(wwd.imageSet3)},
      {prefix: decode(wwd.prefix4), expansion: decode(wwd.imageSet4)}
    ];

    this.objects =
      action.objects.map((o) => new EdObject(
        this,
        rezIndex, levelResources, this.areaSelection,
        new Vec2(o.x, o.y),
        decode(o.imageSet),
        o.id,
      ));

    console.log(`Object count: ${this.objects.length}`);
  }

  static async create(): Promise<Editor> {
    const wwd = await fetchWwd();
    const rezIndex = await fetchRezIndex();
    const resources = await LevelResources.load(rezIndex, 14);
    return new EditorInternal(rezIndex, resources, wwd);
  }

  startAreaSelection(origin: Vec2, destination: Cell<Vec2>): AreaSelection {
    const areaSelection = new AreaSelection(
      this,
      origin,
      destination,
      this.objects,
      () => this._areaSelection.send(new None())
    );
    stopwatch("this._areaSelection.send", () => {
      this._areaSelection.send(new Some(areaSelection));
    });
    return areaSelection;
  }

  selectObjects(objects: ReadonlyArray<EdObject>) {
    this._selectedObjects.send(objects);
  }

  expandShortImageSetId(shortImageSetId: String): Maybe<string> {
    function expandPrefix(prefixEntry: PrefixEntry): Maybe<string> {
      const sanitizedExpansion = prefixEntry.expansion.replace('\\', '_');
      return Maybe.test(shortImageSetId.startsWith(prefixEntry.prefix),
        () => shortImageSetId.replace(prefixEntry.prefix, sanitizedExpansion));
    }

    const expandedPrefixes = this.imageSets.map(expandPrefix);

    return Maybe.findSome(expandedPrefixes);
  }
}
