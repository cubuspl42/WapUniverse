import { fetchRezIndex, RezIndex, RezImage } from "./rezIndex";
import { LevelResources } from "./LevelResources";
import { Cell, CellSink } from "./frp";
import { Vec2 } from "./Vec2";
import { EdObject } from "./EdObject";
import { AreaSelection } from "./AreaSelection";
import { readWorld, World, copyObject } from "./wwd";
import { Maybe, None, Some } from "./Maybe";
import { clamp } from "./utils";
import * as _ from 'lodash';
import { Matrix } from "./Matrix";

const zoomMin = 0.1;
const zoomExponentMin = Math.log2(zoomMin);
const zoomMax = 3;
const zoomExponentMax = Math.log2(zoomMax);

const zoomValues = [0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3];

export const levelIndex = 1;

function correctZoom(inputValue: number): number {
  // return _.minBy(zoomValues, (z) => Math.abs(inputValue - z))!;
  return inputValue;
}


function decode(s: Uint8Array): string {
  return new TextDecoder().decode(s);
}

export class App {
  readonly _editor = new CellSink(EditorInternal.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

export interface Editor {
  readonly levelResources: LevelResources;

  readonly selectedObjects: Cell<ReadonlySet<EdObject>>;

  readonly tiles: Matrix<number>;

  readonly objects: ReadonlyArray<EdObject>;

  readonly areaSelection: Cell<Maybe<AreaSelection>>;

  readonly cameraFocusPoint: Cell<Vec2>;

  readonly cameraZoom: Cell<number>;

  getTileRezImage(tileId: number): Maybe<RezImage>;

  startAreaSelection(origin: Vec2, destination: Cell<Vec2>): AreaSelection;

  zoom(delta: number): void;

  scroll(delta: Vec2): void;
}

async function fetchWwd() {
  const wwd = await fetch("WORLD.WWD");
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
  readonly rezIndex: RezIndex;

  readonly levelResources: LevelResources;

  private readonly _areaSelection = new CellSink<Maybe<AreaSelection>>(new None());

  private readonly _selectedObjects = new CellSink<ReadonlySet<EdObject>>(new Set());

  private _cameraFocusPoint = new CellSink(new Vec2(0, 0));

  private _cameraZoomExponent = new CellSink(1.0);

  readonly tiles: Matrix<number>;

  readonly objects: ReadonlyArray<EdObject>;

  readonly imageSets: ReadonlyArray<PrefixEntry>;

  readonly areaSelection = this._areaSelection as Cell<Maybe<AreaSelection>>;

  readonly selectedObjects = this._selectedObjects as Cell<ReadonlySet<EdObject>>;

  readonly cameraFocusPoint = this._cameraFocusPoint as Cell<Vec2>;

  readonly cameraZoom = this._cameraZoomExponent.map((z) => correctZoom(Math.pow(2, z)));

  private constructor(rezIndex: RezIndex, levelResources: LevelResources, wwd: World) {
    this.levelResources = levelResources;

    const action = _.maxBy(wwd.planes, (p) => p.objects.length)!;

    this.rezIndex = rezIndex;

    this.imageSets = [
      { prefix: decode(wwd.prefix1), expansion: decode(wwd.imageSet1) },
      { prefix: decode(wwd.prefix2), expansion: decode(wwd.imageSet2) },
      { prefix: decode(wwd.prefix3), expansion: decode(wwd.imageSet3) },
      { prefix: decode(wwd.prefix4), expansion: decode(wwd.imageSet4) }
    ];

    this.tiles = new Matrix(action.tilesWide, action.tilesHigh, action.tiles);

    this.objects =
      action.objects.map((o) => {
        const x = copyObject(o, { height: 2 });
        return new EdObject(
          this,
          rezIndex, levelResources, this.areaSelection,
          o,
          new Vec2(o.x, o.y),
          decode(o.imageSet),
          o.id,
        );
      });

    console.log(`Object count: ${this.objects.length}`);

    this._cameraFocusPoint.listen(() => { });
    this._cameraZoomExponent.listen(() => { });
  }

  static async create(): Promise<Editor> {
    const wwd = await fetchWwd();
    const rezIndex = await fetchRezIndex();
    const resources = await LevelResources.load(rezIndex, levelIndex);
    return new EditorInternal(rezIndex, resources, wwd);
  }

  startAreaSelection(origin: Vec2, destination: Cell<Vec2>): AreaSelection {
    const areaSelection = new AreaSelection(
      this,
      origin,
      destination,
      this.objects,
      () => {
        this._areaSelection.send(new None());
      }
    );
    this._areaSelection.send(new Some(areaSelection));

    return areaSelection;
  }

  selectObjects(objects: ReadonlySet<EdObject>) {
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

  getRezImage(imageSetId: string, i: number): Maybe<RezImage> {
    const rezImageSet = this.rezIndex.imageSets[imageSetId];
    if (!rezImageSet) return new None();
    const pidFileName = rezImageSet.frames[i];
    if (!pidFileName) return new None();
    return new Some(rezImageSet.sprites[pidFileName]);
  }

  getTileRezImage(tileId: number): Maybe<RezImage> {
    return new Some(tileId)
      .filter((t) => t >= 0)
      .flatMap((t) => this.getRezImage(`LEVEL${levelIndex}_TILES_ACTION`, tileId));
  }

  // levelIndex

  scroll(delta: Vec2): void {
    const currentFocusPoint = this.cameraFocusPoint.sample();
    console.log(`currentFocusPoint: ${currentFocusPoint} refCount=${this.cameraFocusPoint.getVertex__().refCount()}`);
    const currentZoom = this.cameraZoom.sample();
    const newFocusPoint = currentFocusPoint.sub(delta.div(currentZoom)).floor();
    console.log(`newFocusPoint: ${newFocusPoint}`);
    // console.log(`newFocusPoint: ${newFocusPoint}`);
    this._cameraFocusPoint.send(newFocusPoint);
  }

  zoom(delta: number): void {
    const currentZoomExponent = this._cameraZoomExponent.sample();
    const newZoomExponent = clamp(currentZoomExponent - delta, zoomExponentMin, zoomExponentMax);
    // console.log(`newZoomExponent: ${newZoomExponent}`);
    this._cameraZoomExponent.send(newZoomExponent);
  }
}
