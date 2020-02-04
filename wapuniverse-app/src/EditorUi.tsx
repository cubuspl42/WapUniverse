import React, { useRef, useMemo, useCallback } from 'react';

import './Editor.css';
import { Editor } from "./Editor";
import { AreaSelection } from "./AreaSelection";
import * as PIXI from 'pixi.js';
import { Vec2 } from "./Vec2";
import { Cell, CellSink, LateCellLoop } from "./frp";
import { StreamSink, Stream, Transaction } from "sodiumjs";
import { edObjectSprite } from "./EdObjectUi";
import { elementSize } from "./cellUtils";
import { tileSprite } from "./TileUi";
import { Container, Context, Node } from "./renderer/Renderer";
import * as frp from "./frp/Set";
import { Scene } from './renderer/Scene';
import { SceneResources } from './SceneResources';
import { some, none, Maybe } from './Maybe';

const zoomMultiplier = 0.01;
const scrollMultiplier = 2;

interface EditorUiProps {
  editor: Editor;
}

export function range(end: number): Array<number> {
  return [...Array(end).keys()];
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
  element.addEventListener(type, (e) => {
    e.preventDefault();
    sink.send(e)
  });
  return sink;
}

export const EditorUi = ({ editor }: EditorUiProps) => {
  const m = useMemo(() => {
    return Transaction.run(() => {
      console.log("EditorUi useMemo callback");

      const parentSizeLoop = new LateCellLoop(Vec2.zero);

      const parentSize = parentSizeLoop.cell;
      const offset = parentSize.map((s) => s.div(2).toPixiPoint());
      const focusPoint = editor.cameraFocusPoint.map((f) => f.neg().toPixiPoint());

      const onMouseDown = new StreamSink<React.MouseEvent<HTMLDivElement, MouseEvent>>();
      const onMouseMove = new StreamSink<React.MouseEvent<HTMLDivElement, MouseEvent>>();
      const onMouseUp = new StreamSink<React.MouseEvent<HTMLDivElement, MouseEvent>>();

      const onRmbPressed = onMouseDown.filter((e) => e.button == 2);
      const onMouseMoveP = onMouseMove.map((e) => new Vec2(e.pageX, e.pageY));
      const onRmbUp = onMouseDown.filter((e) => e.button == 2);

      const onRmbDrag = onRmbPressed.map((e) => {
        const p = new Vec2(e.pageX, e.pageY);
        return some(onMouseMoveP.hold(p));
      });
      const onRmbDragStop = onRmbUp.map(() => none<Cell<Vec2>>());

      const rmbDrag = onRmbDragStop.orElse(onRmbDrag).hold(none());

      return {
        parentSizeLoop,
        offset,
        focusPoint,
        onMouseDown,
        onMouseMove,
        onMouseUp,
      }
    })
  }, []);

  const ref = useCallback((parent: HTMLDivElement) => {
    console.log("EditorUi useCallback callback");
    const parentSize = elementSize(parent);

    m.parentSizeLoop.lateLoop(parentSize);

    parent.addEventListener("pointerdown", (e) => {
      console.log("pointerdown");

      const origin = new Vec2(e.clientX, e.clientY);
      const destinationS = new StreamSink<Vec2>();
      const destination = new Cell(origin, destinationS);
      const areaSelection = editor.startAreaSelection(origin, destination);

      const onPointerMove = (e: PointerEvent) => {
        destinationS.send(new Vec2(e.x, e.y));
      };

      const onPointerUp = () => {
        areaSelection.commit();
        parent.removeEventListener("pointermove", onPointerMove);
        parent.removeEventListener("pointerup", onPointerUp);
      };

      parent.addEventListener("pointermove", onPointerMove);
      parent.addEventListener("pointerup", onPointerUp);
    });

    const onWheel = eventStream(parent, "wheel");
    const onWheelCtrl = onWheel.filter((e) => e.ctrlKey);
    const onWheelNoCtrl = onWheel.filter((e) => !e.ctrlKey);

    const deltaV = (e: WheelEvent): Vec2 => {
      return new Vec2(e.deltaX, e.deltaY);
    }

    const sCameraDelta = onWheelNoCtrl.map((e) => {
      return deltaV(e).scale(scrollMultiplier).neg();
    });

    const sZoomDelta = onWheelCtrl.map((e) => {
      console.log(`sZoomDelta callback; deltaY = ${e.deltaY}`);
      return e.deltaY * zoomMultiplier;
    });

    editor.moveCamera.lateLoop(sCameraDelta);
    editor.zoomCamera.lateLoop(sZoomDelta);
  }, []);

  return <div
    ref={ref}
  >
    <Scene buildRoot={(context: Context) => {
      console.log("buildRoot");

      const res = new SceneResources(editor.levelResources);

      const rootChildren = new Set<Node>();
      editor.tiles.forEachIndexed((i, j, t) => {
        rootChildren.add(tileSprite(editor, res, i, j, t));
      });

      editor.objects.forEach((o) => {
        rootChildren.add(edObjectSprite(res, o));
      });

      const root = new Container({
        x: m.offset.map((f) => f.x),
        y: m.offset.map((f) => f.y),
        scale: editor.cameraZoom.map((z) => {
          console.log(`z: ${z}`);
          return new PIXI.Point(z, z);
        }),
        pivot: m.focusPoint,
        children: new frp.Set(rootChildren),
      });

      console.log(`m.focusPoint.refCount = ${m.focusPoint.getVertex__().refCount()}`);
      console.log(`editor.cameraFocusPoint.refCount = ${editor.cameraFocusPoint.getVertex__().refCount()}`);
      console.log(`----`);

      return root;
    }} />
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
