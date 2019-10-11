import React, {useEffect, useState} from 'react';

import './Editor.css';
import {Editor} from "./Editor";
import {Sprite, Stage} from "@inlet/react-pixi";
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

export function EditorUi({editor}: EditorUiProps) {
  const object = editor.object;

  const texture = useCell(object.texture);
  const boundingBox = useCell(object.boundingBox);
  const isHovered = useCell(object.isHovered);

  return <Stage width={1024} height={768}>
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
  </Stage>
}

