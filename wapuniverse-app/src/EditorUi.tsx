import React, {useEffect, useMemo, useState} from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {Container, Sprite, Stage} from "@inlet/react-pixi";
import {GraphicsRectangle} from "./GraphicsRectangle";
import {Cell, StreamSink} from "sodiumjs";
import {Vec2} from "./Vec2";
import {EdObject} from "./EdObject";
import {AreaSelection} from "./AreaSelection";

type CellProvider<T> = () => Cell<T>;

interface EditorUiProps {
  editor: Editor;
}

export function useCell<T>(cell: Cell<T> | CellProvider<T>) {
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
  const isSelected = useCell(object.isSelected);

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
                       strokeColor={
                         isInSelectionArea ? 0xcd0000 :
                           isSelected ? 0xe3fc03 :
                             0x87cefa
                       }/>
    {isHovered && <GraphicsRectangle x={boundingBox.xMin - 2} y={boundingBox.yMin - 2}
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
      if (areaSelectionDestination !== undefined) {
        setAreaSelectionDestination(undefined);
      }
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
