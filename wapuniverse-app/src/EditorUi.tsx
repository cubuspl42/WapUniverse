import React, {useEffect, useMemo, useState} from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {Container, Sprite, Stage} from "@inlet/react-pixi";
import {GraphicsRectangle} from "./GraphicsRectangle";
import {Cell, StreamSink} from "sodiumjs";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";
import * as PIXI from 'pixi.js';
import {Vec2} from "./Vec2";

const Texture = PIXI.Texture;

type CellProvider<T> = () => Cell<T>;

interface EditorUiProps {
  editor: Editor;
}

export function useCell<T>(cell: Cell<T> | CellProvider<T>): T {
  const cell_ = useMemo(
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

function EdObjectUi({object}: EdObjectUiProps): JSX.Element {
  console.log("EdObjectUi(...)");
  const textureM = useCell(object.texture);
  const boundingBox = useCell(object.boundingBox);
  const isHovered = useCell(object.isHovered);
  const isInSelectionArea = useCell(object.isInSelectionArea);
  const isSelected = useCell(object.isSelected);

  return <Container key={object.id}>
    <Sprite key={`s${object.id}`}
            x={boundingBox.xMin}
            y={boundingBox.yMin}
            texture={textureM}
            interactive={true}
            pointerover={() => {
              object.isHovered.send(true);
            }}
            pointerout={() => {
              object.isHovered.send(false);
            }}
            alpha={isHovered ? 1 : 0.5}
    />
    <GraphicsRectangle key={`g1${object.id}`} x={boundingBox.xMin} y={boundingBox.yMin}
                       width={boundingBox.width} height={boundingBox.height}
                       strokeWidth={2}
                       strokeColor={
                         isInSelectionArea ? 0xcd0000 :
                           isSelected ? 0xe3fc03 :
                             0x87cefa
                       }/>

    {isHovered && <GraphicsRectangle key={`g2${object.id}`} x={boundingBox.xMin - 2} y={boundingBox.yMin - 2}
                                     width={boundingBox.width + 4} height={boundingBox.height + 4}
                                     strokeWidth={2}
                                     strokeColor={0x0000cd}/>
    }
  </Container>;
}


export function EditorUi({editor}: EditorUiProps) {
  const areaSelection = useCell(editor.areaSelection);
  const [areaSelectionDestination, setAreaSelectionDestination] =
    useState<StreamSink<Vec2>>();

  return <Stage key={"stage"}
                width={1024}
                height={768}
                onPointerDown={e => {
                  const origin = new Vec2(e.clientX, e.clientY);
                  const destinationS = new StreamSink<Vec2>();
                  setAreaSelectionDestination(destinationS);
                  const destination = new Cell(origin, destinationS);
                  editor.startAreaSelection(origin, destination);
                }}
                onPointerMove={e => {
                  if (areaSelectionDestination !== undefined) {
                    areaSelectionDestination.send(new Vec2(e.clientX, e.clientY));
                  }
                }}
                onPointerUp={e => {
                  if (areaSelectionDestination !== undefined) {
                    setAreaSelectionDestination(undefined);
                  }
                  if (areaSelection != null) {
                    areaSelection.commit();
                  }
                }}
  >
    {
      editor.objects

        .map((o) => <EdObjectUi key={`o${o.id}`} object={o}/>)


    }
    {areaSelection !== null && <AreaSelectionRectangle areaSelection={areaSelection}/>}
  </Stage>;
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
