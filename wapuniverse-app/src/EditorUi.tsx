import React from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {AreaSelection} from "./AreaSelection";
import * as PIXI from 'pixi.js';
import * as pu from "./pixiUtils";
import {Vec2} from "./Vec2";
import {CellSink} from "./Cell";
import {StreamSink} from "sodiumjs";
import {EdObjectUi} from "./EdObjectUi";

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

  private application: PIXI.Application | null = null;

  private divElement: HTMLDivElement | null = null;

  componentDidMount() {
    const parent = this.divElement!;
    const application = pu.autoResizingPixiApplication(parent);
    const objectsContainer = new PIXI.Container();

    parent.addEventListener("pointerdown", (e) => {
      console.log("pointerdown");

      const origin = new Vec2(e.clientX, e.clientY);
      const destinationS = new StreamSink<Vec2>();
      const destination = new CellSink(origin, destinationS);
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

    this.editor.objects.forEach((o) => {
      objectsContainer.addChild(EdObjectUi({object: o}));
    });

    const stage = application.stage;

    stage.addChild(objectsContainer);

    this.editor.areaSelection.forEach(
      (aM) => aM.map((a) => {
        stage.addChild(AreaSelectionRectangle({areaSelection: a}));
      }));


    this.application = application;
  }

  componentWillUnmount() {
    this.application!.destroy(true);
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

export function AreaSelectionRectangle(
  {areaSelection}: AreaSelectionRectangleProps
): PIXI.DisplayObject {
  const rectangle = areaSelection.rectangle;
  return pu.graphicsRectangle({
    x: rectangle.map((r) => r.xMin),
    y: rectangle.map((r) => r.yMin),
    width: rectangle.map((r) => r.width),
    height: rectangle.map((r) => r.height),
    strokeWidth: 1,
    strokeColor: 0x00FFF0,
  });
}
