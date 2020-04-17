import {fetchRezIndex, RezIndex, RezImage} from "../rezIndex";
import {LevelResources} from "../LevelResources";
import {LateStreamLoop, LateCellLoop, switcherK} from "../frp";
import {Vec2} from "../Vec2";
import {EdObject} from "./EdObject";
import {AreaSelection} from "../AreaSelection";
import * as wwd from "../wwd";
import {Maybe, None, Some, none, some} from "../Maybe";
import {clamp} from "../utils";
import _ from 'lodash';
import {Matrix} from "../Matrix";
import {StreamLoop, CellLoop, Operational, Transaction, lambda1, Cell, CellSink, Stream, Unit} from "sodium";
import {World} from "./World";
import {decode} from "../utils/utils";
import {Plane} from "./Plane";
import {ActivePlaneEditor} from "./ActivePlaneEditor";
import {LazyGetter} from 'lazy-get-decorator';
import {GridEntry, GridIndex} from "../GridIndex";
import * as frp from "frp";
import {Rectangle} from "../Rectangle";
import {StreamSink} from "sodiumjs";

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

export interface AreaSelectionInteraction {
  readonly pointerPosition: Cell<Vec2>;

  readonly onEnd: Stream<Unit>;
}

export interface ObjectMovingInteraction {
  readonly pointerPosition: Cell<Vec2>;

  readonly onEnd: Stream<Unit>;
}

export interface ObjectMoving {
  readonly objects: ReadonlySet<EdObject>;
  readonly delta: Cell<Vec2>;
}

export class App {
  readonly _editor = new CellSink(Editor.create());

  readonly editor = this._editor as Cell<Promise<Editor>>;
}

async function fetchWwd() {
  const world = await fetch("ClawEdit/RETAIL03.WWD");
  // const world = await fetch("TEST1.WWD");
  // const world = await fetch("Wreckage.wwd");
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
  console.log(`${s} - Elapsed: ${b - a} ms`);
  return r;
}

function findLevelIndex(name: String) {
  const levelIndexMatch = name.match(/\d+/);
  if (levelIndexMatch == null) throw Error("Level index not present in world name");

  const levelIndex = parseInt(levelIndexMatch[0], 10);
  return levelIndex;
}

class Transform {
  readonly translate: Vec2;
  readonly scale: Vec2;

  constructor(
    translate: Vec2,
    scale: Vec2,
  ) {
    this.translate = translate;
    this.scale = scale;
  }

  transformV(v: Vec2): Vec2 {
    return v.add(this.translate).mulV(this.scale);
  }

  transformR(r: Rectangle): Rectangle {
    return r.map((v) => this.transformV(v));
  }

  invert(): Transform {
    return new Transform(
      this.translate.neg().mulV(this.scale),
      this.scale.inv(),
    );
  }
}

export enum Tool {
  MOVE
}

export class Editor {
  readonly deleteSelectedObjects = new StreamSink<Unit>();

  readonly rezIndex: RezIndex;

  readonly levelResources: LevelResources;

  readonly world: World;

  readonly activePlane: Cell<Plane>;

  readonly activePlaneEditor: Cell<ActivePlaneEditor>;

  readonly moveCamera = new LateStreamLoop<Vec2>();

  readonly zoomCamera = new LateStreamLoop<number>();

  readonly dragCamera = new LateCellLoop(none<CameraDrag>());

  readonly viewportSize = new LateCellLoop(Vec2.zero);

  readonly selectArea = new StreamLoop<AreaSelectionInteraction>();

  readonly moveObjects = new StreamLoop<ObjectMovingInteraction>();

  readonly cameraFocusPoint: Cell<Vec2>;

  readonly cameraZoom: Cell<number>;

  readonly invertedCameraTransform: Cell<Transform>;

  readonly objectGridIndex: GridIndex<EdObject>;

  readonly windowRect: Cell<Rectangle>;

  readonly visibleObjects: frp.Set<EdObject>;

  private switchMoveTool = new StreamSink<Unit>();

  transformInvV(cv: Cell<Vec2>): Cell<Vec2> {
    return this.invertedCameraTransform
      .lift(cv, (t, v) => t.transformV(v));
  }

  @LazyGetter()
  get areaSelection(): Cell<Maybe<AreaSelection>> {
    const buildInteractionCircuit = (asi: AreaSelectionInteraction): Cell<Maybe<AreaSelection>> => {
      const objects = this.activePlane.sample().objects.sample();
      return switcherK(
        some<AreaSelection>(new AreaSelection(
          this.transformInvV(asi.pointerPosition),
          Array.from(objects),
        )),
        asi.onEnd.map(buildIdleCircuit),
      );
    };

    const buildIdleCircuit = (): Cell<Maybe<AreaSelection>> => {
      const onAreaSelect = this.selectArea.once();
      return switcherK(
        none<AreaSelection>(),
        onAreaSelect.map(buildInteractionCircuit),
      );
    };

    return buildIdleCircuit();
  }

  @LazyGetter()
  get objectMoving(): Cell<Maybe<ObjectMoving>> {
    const buildInteractionCircuit = (omi: ObjectMovingInteraction): Cell<Maybe<ObjectMoving>> => {
      const objects = this.selectedObjects.sample();
      const worldPosition = this.transformInvV(omi.pointerPosition);
      const origin = worldPosition.sample();
      const delta = worldPosition.map((w) => w.sub(origin));
      return switcherK(
        some<ObjectMoving>({
          objects, delta,
        }),
        omi.onEnd.map(buildIdleCircuit),
      );
    };

    const buildIdleCircuit = (): Cell<Maybe<ObjectMoving>> => {
      const onAreaSelect = this.moveObjects.once();
      return switcherK(
        none<ObjectMoving>(),
        onAreaSelect.map(buildInteractionCircuit),
      );
    };

    return buildIdleCircuit();
  }

  @LazyGetter()
  get selectedObjects(): Cell<ReadonlySet<EdObject>> {
    const areaSelection = this.areaSelection;
    const selectObjects: Stream<ReadonlySet<EdObject>> =
      Operational.updates(areaSelection)
        .snapshot1(areaSelection)
        .map((mas) =>
          mas.map(
            (as) => as.objectsInArea.sample()
          ).orElse(() => new Set()),
        );

    return selectObjects.hold(new Set());
  }

  @LazyGetter()
  get deleteObjects(): Stream<ReadonlySet<EdObject>> {
    return this.deleteSelectedObjects.snapshot1(this.selectedObjects);
  }

  @LazyGetter()
  get tool(): Cell<Maybe<Tool>> {
    return this.switchMoveTool.accum(
      none(),
      (_, mt) => mt.fold(
        () => some(Tool.MOVE),
        () => none<Tool>(),
      ),
    );
  }

  private constructor(
    rezIndex: RezIndex,
    levelResources: LevelResources,
    levelIndex: number,
    wwdWorld: wwd.World,
  ) {
    this.levelResources = levelResources;
    this.rezIndex = rezIndex;

    const world = new World(this, wwdWorld, levelIndex);

    this.world = world;

    const activePlane = world.planes[1];

    this.activePlane = new Cell(activePlane);
    this.activePlaneEditor = this.activePlane.map((p) => new ActivePlaneEditor(p));

    const buildCameraCircuit = () => Transaction.run(() => {

      const buildFreeCircuit = (focusPoint0: Vec2) => {
        // console.log(`buildFreeCircuit`);

        return this.moveCamera.stream.accum(focusPoint0, (focusPoint, delta) => {
          return focusPoint.add(delta);
        });
      };

      const buildDraggedCircuit = (cameraDrag: CameraDrag, focusPoint0: Vec2) => {
        // Initial pointer position, viewport camera space
        const pointerPosition0 = cameraDrag.pointerPosition.sample();
        // Anchor, world space
        const anchor = focusPoint0.add(pointerPosition0.divS(this.cameraZoom.sample()));
        // console.log(`buildDraggedCircuit: focusPoint0: ${focusPoint0} pointerPosition0: ${pointerPosition0}`);

        return cameraDrag.pointerPosition.lift(this.cameraZoom,
          (pointerPosition, zoom) => {
            // console.log(`anchor: ${anchor} pointerPosition: ${pointerPosition} zoom: ${zoom}`);
            return anchor.sub(pointerPosition.divS(zoom));
          }
        );
      };

      const focusPointLoop = new CellLoop<Vec2>();

      const focusPoint = Cell.switchC(this.dragCamera.cell.map(lambda1((mbCameraDrag) => {
        const focusPoint0 = focusPointLoop.sample();
        return mbCameraDrag.fold(
          () => buildFreeCircuit(focusPoint0),
          (cameraDrag) => buildDraggedCircuit(cameraDrag, focusPoint0),
        );
      }, [focusPointLoop]))).rename("dragCamera/switch");

      const focusPointOut = Operational.value(focusPoint).hold(Vec2.zero);

      focusPointLoop.loop(focusPointOut);

      return focusPointOut;
    });

    this.cameraFocusPoint = buildCameraCircuit();
    // this.cameraFocusPoint.listen((a) => {
    //   console.log(`cameraFocusPoint listen: ${a}`);
    // });

    const buildZoomCircuit = () => {
      const cameraZoomExponent = this.zoomCamera.stream.accum(1, (delta, exponent) => {
        return clamp(exponent - delta, zoomExponentMin, zoomExponentMax);
      });

      return cameraZoomExponent.map((z) => correctZoom(Math.pow(2, z)));
    };

    this.cameraZoom = buildZoomCircuit();
    this.cameraZoom.listen((a) => console.log(`cameraZoom listen: ${a}`));

    const cameraTransform = this.cameraFocusPoint.lift(this.cameraZoom,
      (fp, z) => new Transform(fp.neg(), new Vec2(z, z)),
    );

    const invertedCameraTransform = cameraTransform.map((t) => t.invert());

    this.invertedCameraTransform = invertedCameraTransform;

    const objectGridIndex = new GridIndex(
      activePlane.objects.map<GridEntry<EdObject>>(
        o => ({
          area: o.boundingBox,
          element: o,
        }),
      )
    );

    this.objectGridIndex = objectGridIndex;

    const viewportRect = this.viewportSize.cell.map(
      vs => Rectangle.fromCenter(Vec2.zero, vs),
    );

    const windowRect = viewportRect.lift(invertedCameraTransform,
      (vr, ct) => {
        const r = ct.transformR(vr);
        // console.log({ct});
        // console.log(`Viewport: ${vr}, window: ${r}`);
        return r;
      },
    );

    this.windowRect = windowRect;

    const visibleObjects = objectGridIndex.query(windowRect);

    this.visibleObjects = visibleObjects;
  }

  static async create(): Promise<Editor> {
    const wwd = await fetchWwd();
    const rezIndex = await fetchRezIndex();
    const levelIndex = findLevelIndex(decode(wwd.name));
    const resources = await LevelResources.load(rezIndex, levelIndex);
    return new Editor(rezIndex, resources, levelIndex, wwd);
  }

  doDeleteSelectedObjects(): void {
    this.deleteSelectedObjects.send(Unit.UNIT);
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
  doSwitchMoveTool(): void {
    this.switchMoveTool.send(Unit.UNIT);
  }
}
