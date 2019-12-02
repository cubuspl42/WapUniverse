import React from 'react';

import './Editor.css';
import {EdObject} from "./EdObject";
import * as PIXI from 'pixi.js';
import * as pu from "./pixiUtils";
import {CellSink} from "./Cell";


interface EdObjectUiProps {
  object: EdObject;
}

export function EdObjectUi({object}: EdObjectUiProps): PIXI.DisplayObject {
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
