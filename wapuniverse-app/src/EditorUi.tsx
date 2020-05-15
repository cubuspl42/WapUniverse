import React, {useRef, useMemo, useCallback} from 'react';

import './Editor.css';
import {
  Editor,
  CameraDrag,
  Tool,
  AreaSelectionInteraction,
  ObjectMovingInteraction,
} from "./editor/Editor";
import {AreaSelection} from "./AreaSelection";
import * as PIXI from 'pixi.js';
import {Vec2} from "./Vec2";
import {Cell, CellSink, lambda1} from "sodium";
import {StreamSink, Stream, Transaction, Operational} from "sodium";
import {edObjectSprite} from "./EdObjectUi";
import {elementSize} from "./cellUtils";
import {tileSprite} from "./TileUi";
import {Container, Context, GraphicRectangle, Node} from "./renderer/Renderer";
import * as frp from "./frp/Set";
import {Scene} from './renderer/Scene';
import {SceneResources} from './SceneResources';
import {some, none, Maybe} from './Maybe';
import {planeNode} from './PlaneUi';
import {LateCellLoop, switcherK} from "./frp";
import {Rectangle} from "./Rectangle";
import Button from '@material-ui/core/Button';
import DeleteIcon from '@material-ui/icons/Delete';
import {OpenWith} from "@material-ui/icons";
import ToggleButton from '@material-ui/lab/ToggleButton';
import {useCell} from "./hooks";
import {MaybeEditObjectDialog} from "./EditObjectDialog";
import EditIcon from '@material-ui/icons/Edit';

const zoomMultiplier = 0.01;
const scrollMultiplier = 2;

interface EditorUiProps {
  editor: Editor;
}

function streamCallback<T>(): [Stream<T>, (event: T) => void] {
  const sink = new StreamSink<T>();
  const callback = (event: T) => sink.send(event);
  return [sink, callback];
}

function eventStream<K extends keyof HTMLElementEventMap>(
  element: HTMLElement, type: K
): Stream<HTMLElementEventMap[K]> {
  const sink = new StreamSink<HTMLElementEventMap[K]>();
  sink.rename("eventStream/sink");
  sink.listen(() => {
  });
  element.addEventListener(type, (e) => {
    e.preventDefault();
    // if (type == "mousedown" && (e as any).button === 0) Transaction.enableDebug(true);
    // if (type == "mouseup") Transaction.enableDebug(true);
    // if (Math.random() < 0.01) Transaction.enableDebug(true);
    sink.send(e);
    // Transaction.enableDebug(false);
  }, false);
  return sink;
}

interface MouseDragInteraction {
  readonly position: Cell<Vec2>;
}

function elementV(e: MouseEvent): Vec2 {
  const rect = (e.target as HTMLElement).getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  return new Vec2(x, y);
}

function buildMouseDragCircuit(element: HTMLElement, button: number): Cell<Maybe<MouseDragInteraction>> {
  const mouseRightUp = right(eventStream(element, "mouseup"));
  const mouseRightDown = right(eventStream(element, "mousedown"));
  const mouseMove = eventStream(element, "mousemove");

  function right(s: Stream<MouseEvent>) {
    return s.filter((e) => e.button == button);
  }

  function buildInteractionCircuit(e: MouseEvent): Cell<Maybe<MouseDragInteraction>> {
    const onMouseRightUp = mouseRightUp.once();
    return switcherK(
      some({
        position: mouseMove.map(elementV).hold(elementV(e)),
      }),
      onMouseRightUp.map(buildIdleCircuit),
    ).rename("interactionCircuit");
  }

  function buildIdleCircuit(): Cell<Maybe<MouseDragInteraction>> {
    const onMouseRightDown = mouseRightDown.once();
    return switcherK(
      none<MouseDragInteraction>(),
      onMouseRightDown.map(buildInteractionCircuit),
    ).rename("idleCircuit");
  }

  return buildIdleCircuit();
}

export const EditorUi = ({editor}: EditorUiProps) => {
  const world = editor.world;

  const m = useMemo(() => {
    return Transaction.run(() => {
      console.log("EditorUi useMemo callback");

      const parentSizeLoop = new LateCellLoop(Vec2.zero);

      const parentSize = parentSizeLoop.cell;

      parentSize.listen((p) => console.log(`parentSize.listen [memo] p = ${p}`));

      const offset = parentSize.map((s) => s.div(2)).rename("offset");

      offset.listen((p) => console.log(`offset.listen [memo] p = ${p}`));

      const focusPoint = editor.cameraFocusPoint.map((f) => f.toPixiPoint());

      return {
        parentSizeLoop,
        offset,
        focusPoint,
        // onMouseDown,
        // onMouseMove,
        // onMouseUp,
      }
    })
  }, [editor]);

  const ref = useCallback((parent: HTMLDivElement) => {
    console.log("EditorUi useCallback callback", parent);

    // if (parent === null) return;

    const parentSize = elementSize(parent);

    parentSize.listen((p) => console.log(`parentSize.listen p = ${p}`));

    m.parentSizeLoop.lateLoop(parentSize);
    editor.viewportSize.lateLoop(parentSize);

    // parent.addEventListener("pointerdown", (e) => {
    //   console.log("pointerdown");

    //   const origin = new Vec2(e.clientX, e.clientY);
    //   const destinationS = new StreamSink<Vec2>();
    //   const destination = new Cell(origin, destinationS);
    //   const areaSelection = editor.startAreaSelection(origin, destination);

    //   const onPointerMove = (e: PointerEvent) => {
    //     destinationS.send(new Vec2(e.x, e.y));
    //   };

    const onPointerDown = () => {
      console.log(`Number of visible objects: ${editor.visibleObjects.sample().size}`);
      console.log(`Number of visited vertices: ${Transaction.visitedVerticesCount}`);
    };

    parent.addEventListener("pointerdown", onPointerDown);
    // });

    const onWheel = eventStream(parent, "wheel");
    const onWheelCtrl = onWheel.filter((e) => e.ctrlKey);
    const onWheelNoCtrl = onWheel.filter((e) => !e.ctrlKey);

    const deltaV = (e: WheelEvent): Vec2 => {
      return new Vec2(e.deltaX, e.deltaY);
    };

    const sCameraDelta = onWheelNoCtrl.map((e) => {
      return deltaV(e).mulS(scrollMultiplier);
    });

    const sZoomDelta = onWheelCtrl.map((e) => {
      console.log(`sZoomDelta callback; deltaY = ${e.deltaY}`);
      return e.deltaY * zoomMultiplier;
    });

    editor.moveCamera.lateLoop(sCameraDelta);
    editor.zoomCamera.lateLoop(sZoomDelta);

    const mouseDrag = buildMouseDragCircuit(parent, 2);

    const cameraDrag = mouseDrag.map((md) =>
      md.map<CameraDrag>((d) => ({
        pointerPosition: m.offset.lift(d.position,
          (o, p) => p.sub(o)
        ),
      }))
    );

    editor.dragCamera.lateLoop(cameraDrag);

    const mouseDragLeft = buildMouseDragCircuit(parent, 0);

    const transformV = (v: Cell<Vec2>): Cell<Vec2> =>
      m.offset.lift(v, (o, p) => p.sub(o));

    // function filter<A>(ca: Cell<A>, test: (a: A) => boolean): Cell<Maybe<A>> {
    //  return  Operational.updates(ca).filter(test).hold(ca.sample());
    // }

    const onMouseDragLeft = Operational.value(mouseDragLeft);

    const selectArea = onMouseDragLeft
      .filter((mdi) => mdi.isSome() && editor.tool.sample().isNone())
      .map((mdi): AreaSelectionInteraction => ({
        pointerPosition: transformV(mdi.get().position),
        onEnd: onMouseDragLeft.once(),
      }));

    editor.selectArea.loop(selectArea);

    const moveObjects = onMouseDragLeft
      .filter((mdi) => mdi.isSome() && editor.tool.sample().fold(
        () => false,
        (t) => t === Tool.MOVE),
      )
      .map((mdi): ObjectMovingInteraction => ({
        pointerPosition: transformV(mdi.get().position),
        onEnd: onMouseDragLeft.once(),
      }));

    editor.moveObjects.loop(moveObjects);
  }, [editor]);

  const tool = useCell(editor.tool);

  console.log({tool, eq: tool.equals(some(Tool.MOVE))});

  useCell(editor.pin);

  return <div>
    <div id="toolbar">
      <Button
        variant="contained"
        onClick={() => editor.doDeleteSelectedObjects()}
      >
        <DeleteIcon/>
      </Button>
      <Button
        variant="contained"
        onClick={() => editor.doEditObject()}
      >
        <EditIcon/>
      </Button>
      <ToggleButton
        value="check"
        selected={tool === some(Tool.MOVE)}
        onChange={() => editor.doSwitchMoveTool()}
      >
        <OpenWith/>
      </ToggleButton>
    </div>
    <div ref={ref}>
      <Scene buildRoot={(context: Context) => {
        console.log("buildRoot");

        const res = new SceneResources(editor.levelResources);

        const rootChildren = new Set<Node>();

        world.planes.forEach((p) => rootChildren.add(planeNode(res, p)));

        const r = editor.areaSelection.flatMap(ma =>
          ma
            .map(a => a.rectangle)
            .getOrElse(() => new Cell(new Rectangle(Vec2.zero, Vec2.zero))),
        );
        rootChildren.add(new GraphicRectangle({
          x: r.map(r => r.xMin),
          y: r.map(r => r.yMin),
          width: r.map(r => r.width),
          height: r.map(r => r.height),
        }));

        const root = new Container({
          x: m.offset.map((f) => f.x),
          y: m.offset.map((f) => f.y),
          scale: editor.cameraZoom.map((z) => {
            console.log(`z: ${z}`);
            return new PIXI.Point(z, z);
          }),
          pivot: m.focusPoint,
          children: frp.Set.hold(rootChildren),
        });

        return root;
      }}/>
    </div>
    <MaybeEditObjectDialog editor={editor}/>
  </div>
};

interface AreaSelectionRectangleProps {
  areaSelection: AreaSelection;
}

// export function AreaSelectionRectangle(
//   {areaSelection}: AreaSelectionRectangleProps
// ): PIXI.DisplayObject {
//   const rectangle = areaSelection.rectangle;
//   return pu.graphicsRectangle({
//     x: rectangle.map((r) => r.xMin),
//     y: rectangle.map((r) => r.yMin),
//     width: rectangle.map((r) => r.width),
//     height: rectangle.map((r) => r.height),
//     strokeWidth: 1,
//     strokeColor: 0x00FFF0,
//   });
// }
