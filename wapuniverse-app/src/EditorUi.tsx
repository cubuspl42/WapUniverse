import React, {useEffect, useMemo, useState} from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {GraphicsRectangle} from "./GraphicsRectangle";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";
import * as PIXI from 'pixi.js';
import * as pu from "./pixiUtils";
import {Vec2} from "./Vec2";
import {Cell, CellSink} from "./Cell";
import {StreamSink} from "sodiumjs";

const Texture = PIXI.Texture;

type CellProvider<T> = () => Cell<T>;

interface EditorUiProps {
  editor: Editor;
}

export function useCell<T>(cell: Cell<T> | CellProvider<T>): T {
  const cell_ = useMemo<Cell<T>>(
    cell instanceof Cell ? () => {
      return cell;
    } : cell, []);

  const [value, setValue] = useState(cell_.sample());

  useEffect(() => {
    return cell_.listen(setValue);
  }, []);

  return value;
}

export function range(end: number): Array<number> {
  return [...Array(end).keys()];
}

function getRandomInt(max: number) {
  let min = 0;
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

interface EdObjectUiProps {
  object: EdObject;
}

function EdObjectUi({object}: EdObjectUiProps): PIXI.DisplayObject {
  const texture = object.texture;
  const boundingBox = object.boundingBox;
  const isHovered = object.isHovered;
  const isInSelectionArea = object.isInSelectionArea;
  const isSelected = object.isSelected;

  const x = boundingBox.map((b) => b.xMin);
  const y = boundingBox.map((b) => b.yMin);
  const width = boundingBox.map((b) => b.width);
  const height = boundingBox.map((b) => b.height);

  const container = new PIXI.Container();

  const sprite = pu.sprite({
    x: x,
    y: y,
    texture: texture,
    alpha: isHovered.map((h): number => h ? 1 : 0.5),
  });

  sprite.interactive = true;

  sprite.on("pointerdown", () => {
    const newI = object.i.sample() + 1;
    console.log(`newI: ${newI}`);
    object.i.send(newI);
  });

  sprite.on("pointerover", () => {
    object.isHovered.send(true);
  });

  sprite.on("pointerout", () => {
    object.isHovered.send(false);
  });

  container.addChild(sprite);

  const frameRectangle = pu.graphicsRectangle({
    x: x,
    y: y,
    width: width,
    height: height,
    strokeWidth: new CellSink(2),
    strokeColor: isInSelectionArea.lift(isSelected,
      (isInSelectionAreaV: boolean, isSelectedV: boolean): number =>
        isInSelectionAreaV ? 0xcd0000 : isSelectedV ? 0xe3fc03 : 0x87cefa
    ),
  });

  container.addChild(frameRectangle);

  {/*{isHovered && <GraphicsRectangle key={`g2${object.id}`} x={boundingBox.xMin - 2} y={boundingBox.yMin - 2}*/
  }
  {/*                                 width={boundingBox.width + 4} height={boundingBox.height + 4}*/
  }
  {/*                                 strokeWidth={2}*/
  }
  {/*                                 strokeColor={0x0000cd}/>*/
  }
  {/*}*/
  }

  return container;
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

    application.stage.addChild(objectsContainer);

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
) {
  const rectangle = useCell(areaSelection.rectangle);
  return <GraphicsRectangle x={rectangle.xMin} y={rectangle.yMin}
                            width={rectangle.width} height={rectangle.height}
                            strokeWidth={1} strokeColor={0x00FFF0}/>
}
