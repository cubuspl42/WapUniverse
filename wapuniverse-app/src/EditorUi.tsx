import React, { useRef, useMemo, useCallback } from 'react';

import './Editor.css';
import { Editor } from "./Editor";
import { AreaSelection } from "./AreaSelection";
import * as PIXI from 'pixi.js';
import { Vec2 } from "./Vec2";
import { Cell, CellSink, LateCellLoop } from "./frp";
import { StreamSink, Stream } from "sodiumjs";
import { edObjectSprite } from "./EdObjectUi";
import { elementSize } from "./cellUtils";
import { tileSprite } from "./TileUi";
import { Container, Context, Node } from "./renderer/Renderer";
import * as frp from "./frp/Set";
import { Scene } from './renderer/Scene';

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

export const EditorUi = ({ editor }: EditorUiProps) => {
  const m = useMemo(() => {
    console.log("EditorUi useMemo callback");

    const parentSizeLoop = new LateCellLoop(Vec2.zero);

    const parentSize = parentSizeLoop.cell;
    const offset = parentSize.map((s) => s.div(2).toPixiPoint());
    const focusPoint = editor.cameraFocusPoint.map((f) => f.neg().toPixiPoint());

    return {
      parentSizeLoop,
      offset,
      focusPoint,
    }
  }, []);

  const ref = useCallback((parent: HTMLDivElement) => {
    console.log("EditorUi useCallback callback");
    m.parentSizeLoop.lateLoop(elementSize(parent));

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

    parent.addEventListener("wheel", (e) => {
      e.preventDefault();
      if (e.ctrlKey) {
        editor.zoom(e.deltaY * zoomMultiplier);
      } else {
        editor.scroll(new Vec2(
          e.deltaX * scrollMultiplier,
          e.deltaY * scrollMultiplier,
        ));
      }
    });
  }, []);

  return <div ref={ref}>
    <Scene buildRoot={(context: Context) => {
      console.log("buildRoot");

      const rootChildren = new Set<Node>();
      editor.tiles.forEachIndexed((i, j, t) => {
        rootChildren.add(tileSprite(editor.levelResources, i, j, t));
      });

      editor.objects.forEach((o) => {
        rootChildren.add(edObjectSprite(o));
      });

      console.log(`m.focusPoint.refCount = ${m.focusPoint.getVertex__().refCount()}`);
      console.log(`editor.cameraFocusPoint.refCount = ${editor.cameraFocusPoint.getVertex__().refCount()}`);

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
