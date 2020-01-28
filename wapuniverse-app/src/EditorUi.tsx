import React from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {AreaSelection} from "./AreaSelection";
import * as PIXI from 'pixi.js';
import {Vec2} from "./Vec2";
import {Cell, CellSink, eventStream} from "./frp";
import {Stream, StreamSink} from "sodiumjs";
import {edObjectSprite} from "./EdObjectUi";
import {elementSize} from "./cellUtils";
import {tileSprite} from "./TileUi";
import {Container, Context, Node} from "./renderer/Renderer";
import * as frp from "./frp/Set";

const zoomMultiplier = 0.01;
const scrollMultiplier = 2;

interface EditorUiProps {
  editor: Editor;
}

export function range(end: number): Array<number> {
  return [...Array(end).keys()];
}

export class EditorUi extends React.Component<EditorUiProps> {
  private get editor(): Editor {
    return this.props.editor;
  }

  // private application: PIXI.Application | null = null;

  private divElement: HTMLDivElement | null = null;

  componentDidMount() {
    console.log("EditorUi.componentDidMount");

    const parent = this.divElement!;

    const context = new Context({
      parent: parent,
    });

    // const application = pu.autoResizingPixiApplication(parent);
    // const stage = application.stage;

    const parentSize = elementSize(parent);
    const offset = parentSize.map((s) => s.div(2).toPixiPoint());
    const focusPoint = this.editor.cameraFocusPoint.map((f) => f.neg().toPixiPoint());

    // const rootContainer = new Container({
    //   x: offset.map((f) => f.x),
    //   y: offset.map((f) => f.y),
    //   scale: this.editor.cameraZoom.map((z) => {
    //     console.log(`z: ${z}`);
    //     return new PIXI.Point(z, z);
    //   }),
    //   pivot: focusPoint,
    // });

    const borderTexture = PIXI.Texture.from("border.png");

    parent.addEventListener("pointerdown", (e) => {
      console.log("pointerdown");

      const origin = new Vec2(e.clientX, e.clientY);
      const destinationS = new StreamSink<Vec2>();
      const destination = new Cell(origin, destinationS);
      const areaSelection = this.editor.startAreaSelection(origin, destination);

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
        this.editor.zoom(e.deltaY * zoomMultiplier);
      } else {
        this.editor.scroll(new Vec2(
          e.deltaX * scrollMultiplier,
          e.deltaY * scrollMultiplier,
        ));
      }
    });

    eventStream(parent, "mousedown")
      .filter((e) => e.button === 2);


    const rootChildren = new Set<Node>();
    this.editor.tiles.forEachIndexed((i, j, t) => {
      rootChildren.add(tileSprite(this.editor.levelResources, i, j, t));
    });

    this.editor.objects.forEach((o) => {
      rootChildren.add(edObjectSprite(o));
    });

    // this.editor.objects.forEach((o) => {
    //   rootChildren.add(edObjectBorder(o, borderTexture));
    // });

    const root = new Container({
      x: offset.map((f) => f.x),
      y: offset.map((f) => f.y),
      scale: this.editor.cameraZoom.map((z) => {
        console.log(`z: ${z}`);
        return new PIXI.Point(z, z);
      }),
      pivot: focusPoint,
      children: new frp.Set(rootChildren),
    });

    context.setRoot(root);

    // this.application = application;
  }

  componentWillUnmount() {
    // this.application!.destroy(true);
  }

  render() {
    return <div
      onPointerDown={() => {
        // this.cell.send(Math.random());
      }}
      className={"Editor"} ref={el => this.divElement = el}
    />;
  }

  // _render() {
  //   const areaSelection = useCell(this.editor.areaSelection);
  //   const [areaSelectionDestination, setAreaSelectionDestination] =
  //     useState<StreamSink<Vec2>>();
  //   return <Stage key={"stage"}
  //                 width={1024}
  //                 height={768}
  //                 onPointerDown={e => {
  //                   const origin = new Vec2(e.clientX, e.clientY);
  //                   const destinationS = new StreamSink<Vec2>();
  //                   setAreaSelectionDestination(destinationS);
  //                   const destination = new Cell(origin, destinationS);
  //                   editor.startAreaSelection(origin, destination);
  //                 }}
  //                 onPointerMove={e => {
  //                   if (areaSelectionDestination !== undefined) {
  //                     areaSelectionDestination.send(new Vec2(e.clientX, e.clientY));
  //                   }
  //                 }}
  //                 onPointerUp={e => {
  //                   if (areaSelectionDestination !== undefined) {
  //                     setAreaSelectionDestination(undefined);
  //                   }
  //                   if (areaSelection != null) {
  //                     areaSelection.commit();
  //                   }
  //                 }}
  //   >
  //     {
  //       editor.objects
  //
  //         .map((o) => <EdObjectUi key={`o${o.id}`} object={o}/>)
  //
  //
  //     }
  //     {areaSelection !== null && <AreaSelectionRectangle areaSelection={areaSelection}/>}
  //   </Stage>;
  // }
}

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
