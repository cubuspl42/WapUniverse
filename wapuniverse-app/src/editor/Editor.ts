import { fetchRezIndex, RezIndex, RezImage } from "../rezIndex";
import { LevelResources } from "../LevelResources";
import { Cell, CellSink, LateStreamLoop, LateCellLoop } from "../frp";
import { Vec2 } from "../Vec2";
import { EdObject } from "./EdObject";
import { AreaSelection } from "../AreaSelection";
import * as wwd from "../wwd";
import { Maybe, None, Some, none } from "../Maybe";
import { clamp } from "../utils";
import _ from 'lodash';
import { Matrix } from "../Matrix";
import { StreamLoop, CellLoop, Operational, Transaction, lambda1 } from "sodiumjs";
import { World } from "./World";
import { decode } from "../utils/utils";

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

export interface CameraDrag {
  readonly pointerPosition: Cell<Vec2>;
}

export class App {
  readonly _editor = new CellSink(Editor.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

async function fetchWwd() {
  const world = await fetch("ClawEdit/RETAIL08.WWD");
  const blob = await world.blob();
  const arrayBuffer = await blob.arrayBuffer();
  return wwd.readWorld(arrayBuffer);
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

  readonly world: World;

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
    wwdWorld: wwd.World,
  ) {
    this.levelResources = levelResources;
    this.rezIndex = rezIndex;

    this.world = new World(this, wwdWorld, levelIndex);

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

  // startAreaSelection(origin: Vec2, destination: Cell<Vec2>): AreaSelection {
  //   const areaSelection = new AreaSelection(
  //     this,
  //     origin,
  //     destination,
  //     this.objects,
  //     () => {
  //       this._areaSelection.send(new None());
  //     }
  //   );
  //   this._areaSelection.send(new Some(areaSelection));

  //   return areaSelection;
  // }

  selectObjects(objects: ReadonlySet<EdObject>) {
    this._selectedObjects.send(objects);
  }


}
