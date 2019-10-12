import React, {useEffect, useState} from 'react';

import './Editor.css';
import {Editor, EdObject} from "./Editor";
import {Container, Sprite, Stage} from "@inlet/react-pixi";
import {GraphicsRectangle} from "./GraphicsRectangle";
import {Cell} from "sodiumjs";

interface EditorUiProps {
  editor: Editor;
}

function useCell<T>(cell: Cell<T>) {
  const [value, setValue] = useState(cell.sample());

  useEffect(() => {
    return cell.listen(setValue);
  }, [cell]);

  return value;
}

function EdObjectUi(object: EdObject) {
  const texture = useCell(object.texture);
  const boundingBox = useCell(object.boundingBox);
  const isHovered = useCell(object.isHovered);
  return <Container>
    <Sprite
      x={boundingBox.x}
      y={boundingBox.y}
      texture={texture}
      interactive={true}
      pointerover={() => {
        object.isHovered.send(true);
      }}
      pointerout={() => {
        object.isHovered.send(false);
      }}
    />
    <GraphicsRectangle x={boundingBox.x} y={boundingBox.y} width={boundingBox.width} height={boundingBox.height}
                       strokeWidth={2}
                       strokeColor={isHovered ? 0xFF0000 : 0x0000AA}/>
  </Container>;
}

export function EditorUi({editor}: EditorUiProps) {
  return <Stage width={1024} height={768}>
    {editor.objects.map(EdObjectUi)}
  </Stage>
}

