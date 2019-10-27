import React, {useEffect, useMemo, useState} from 'react';

import './Editor.css';
import {AreaSelection, Editor, EdObject} from "./Editor";
import {Container, Sprite, Stage} from "@inlet/react-pixi";
import {GraphicsRectangle} from "./GraphicsRectangle";
import {Cell, StreamSink} from "sodiumjs";
import {Vec2} from "./Vec2";

type CellProvider<T> = () => Cell<T>;

interface EditorUiProps {
  editor: Editor;
}

function useCell<T>(cell: Cell<T> | CellProvider<T>) {
  const cell_ = useMemo(
    cell instanceof Cell ? () => cell : cell,
    undefined);
  const [value, setValue] = useState(cell_.sample());

  useEffect(() => cell_.listen(setValue));

  return value;
}

function EdObjectUi(object: EdObject) {
  const texture = useCell(object.texture);
  const boundingBox = useCell(object.boundingBox);
  const isHovered = useCell(object.isHovered);
  const isInSelectionArea = useCell(object.isInSelectionArea);

  return <Container>
    <Sprite
      x={boundingBox.xMin}
      y={boundingBox.yMin}
      texture={texture}
      interactive={true}
      pointerover={() => {
        object.isHovered.send(true);
      }}
      pointerout={() => {
        object.isHovered.send(false);
      }}
    />
    <GraphicsRectangle x={boundingBox.xMin} y={boundingBox.yMin}
                       width={boundingBox.width} height={boundingBox.height}
                       strokeWidth={2}
                       strokeColor={isInSelectionArea ? 0xcd0000 :
                         isHovered ? 0x0000cd : 0x87cefa
                       }/>
  </Container>;
}


export function EditorUi({editor}: EditorUiProps) {
  const areaSelection = useCell(editor.areaSelection);
  const [areaSelectionDestination, setAreaSelectionDestination] =
    useState<StreamSink<Vec2>>();

  return <Stage
    width={1024}
    height={768}
    onPointerDown={e => {
      const origin = new Vec2(e.clientX, e.clientY);
      const destinationS = new StreamSink<Vec2>();
      setAreaSelectionDestination(destinationS);
      const destination = new Cell(origin, destinationS);
      editor.selectByArea(origin, destination);
    }}
    onPointerMove={e => {
      if (areaSelectionDestination !== undefined) {
        areaSelectionDestination.send(new Vec2(e.clientX, e.clientY));
      }
    }}
    onPointerUp={e => {
      if (areaSelection != null) {
        areaSelection.commit();
      }
    }}
  >
    {editor.objects.map(EdObjectUi)}
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
