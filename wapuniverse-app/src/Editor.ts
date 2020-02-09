import { fetchRezIndex, RezIndex, RezImage } from "./rezIndex";
import { LevelResources } from "./LevelResources";
import { Cell, CellSink, LateStreamLoop, LateCellLoop } from "./frp";
import { Vec2 } from "./Vec2";
import { EdObject } from "./EdObject";
import { AreaSelection } from "./AreaSelection";
import { readWorld, World, copyObject } from "./wwd";
import { Maybe, None, Some, none } from "./Maybe";
import { clamp } from "./utils";
import * as _ from 'lodash';
import { Matrix } from "./Matrix";
import { StreamLoop, CellLoop, Operational, Transaction, lambda1 } from "sodiumjs";

const zoomMin = 0.1;
const zoomExponentMin = Math.log2(zoomMin);
const zoomMax = 3;
const zoomExponentMax = Math.log2(zoomMax);

const zoomValues = [0.25, 0.5, 0.75, 1, 1.5, 2, 2.5, 3];

const levelIndexRegex = /\d+/;

function correctZoom(inputValue: number): number {
  // return _.minBy(zoomValues, (z) => Math.abs(inputValue - z))!;
  return inputValue;
}


function decode(s: Uint8Array): string {
  return new TextDecoder().decode(s);
}

export interface CameraDrag {
  readonly pointerPosition: Cell<Vec2>;
}

export class App {
  readonly _editor = new CellSink(Editor.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

async function fetchWwd() {
  const wwd = await fetch("ClawEdit/RETAIL08.WWD");
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

function findLevelIndex(name: String) {
  const levelIndexMatch = name.match(/\d+/);
  if (levelIndexMatch == null) throw Error("Level index not present in world name");

  const levelIndex = parseInt(levelIndexMatch[0], 10);
  return levelIndex;
}

export class Editor {
  readonly rezIndex: RezIndex;

  readonly levelResources: LevelResources;

  private readonly _areaSelection = new CellSink<Maybe<AreaSelection>>(new None());

  private readonly _selectedObjects = new CellSink<ReadonlySet<EdObject>>(new Set());

  readonly levelIndex: number;

  readonly tiles: Matrix<number>;

  readonly objects: ReadonlyArray<EdObject>;

  readonly imageSets: ReadonlyArray<PrefixEntry>;

  readonly areaSelection = this._areaSelection as Cell<Maybe<AreaSelection>>;

  readonly selectedObjects = this._selectedObjects as Cell<ReadonlySet<EdObject>>;

  readonly moveCamera = new LateStreamLoop<Vec2>();

  readonly zoomCamera = new LateStreamLoop<number>();

  readonly dragCamera = new LateCellLoop(none<CameraDrag>());

  readonly cameraFocusPoint: Cell<Vec2>;

  readonly cameraZoom: Cell<number>;

  private constructor(
    rezIndex: RezIndex,
    levelResources: LevelResources,
    levelIndex: number,
    wwd: World,
  ) {
    this.levelResources = levelResources;
    this.rezIndex = rezIndex;

    const levelIndexMatch = decode(wwd.name).match(/\d+/);
    if (levelIndexMatch == null) throw Error("Level index not present in world name");

    const action = _.maxBy(wwd.planes, (p) => p.objects.length)!;

    this.levelIndex = levelIndex;

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

    const buildCameraCircuit = () => Transaction.run(() => {
      const focusPointLoop = new CellLoop<Vec2>();

      const buildFreeCircuit = (focusPoint0: Vec2) => {
        return this.moveCamera.stream.accum(focusPoint0, (focusPoint, delta) => {
          return focusPoint.add(delta);
        });
      };

      const buildDraggedCircuit = (cameraDrag: CameraDrag, focusPoint0: Vec2) => {
        // Initial pointer position, viewport camera space
        const pointerPosition0 = cameraDrag.pointerPosition.sample();
        // Anchor, world space
        const anchor = focusPoint0.add(pointerPosition0.divS(this.cameraZoom.sample()));
        console.log(`focusPoint0: ${focusPoint0} pointerPosition0: ${pointerPosition0}`);

        return cameraDrag.pointerPosition.lift(this.cameraZoom,
          (pointerPosition, zoom) => {
            console.log(`anchor: ${anchor} pointerPosition: ${pointerPosition} zoom: ${zoom}`);
            return anchor.sub(pointerPosition.divS(zoom));
          }
        );
      };

      const focusPoint = Cell.switchC(this.dragCamera.cell.map(lambda1((mbCameraDrag) => {
        const focusPoint0 = focusPointLoop.sample();
        return mbCameraDrag.fold(
          () => buildFreeCircuit(focusPoint0),
          (cameraDrag) => buildDraggedCircuit(cameraDrag, focusPoint0),
        );
      }, [focusPointLoop])));

      const focusPointOut = Operational.value(focusPoint).hold(Vec2.zero);

      focusPointLoop.loop(focusPointOut);

      return focusPointOut;
    });

    this.cameraFocusPoint = buildCameraCircuit();
    this.cameraFocusPoint.listen((a) => console.log(`cameraFocusPoint listen: ${a}`));

    const buildZoomCircuit = () => {
      const cameraZoomExponent = this.zoomCamera.stream.accum(1, (delta, exponent) => {
        return clamp(exponent - delta, zoomExponentMin, zoomExponentMax);
      });

      return cameraZoomExponent.map((z) => correctZoom(Math.pow(2, z)));
    };

    this.cameraZoom = buildZoomCircuit();
    this.cameraZoom.listen((a) => console.log(`cameraZoom listen: ${a}`));
  }

  static async create(): Promise<Editor> {
    const wwd = await fetchWwd();
    const rezIndex = await fetchRezIndex();
    const levelIndex = findLevelIndex(decode(wwd.name));
    const resources = await LevelResources.load(rezIndex, levelIndex);
    return new Editor(rezIndex, resources, levelIndex, wwd);
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
      .flatMap((t) => this.getRezImage(`LEVEL${this.levelIndex}_TILES_ACTION`, tileId));
  }
}
