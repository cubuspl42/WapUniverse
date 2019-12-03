import React from 'react';

import './Editor.css';
import {EdObject} from "./EdObject";
import * as PIXI from 'pixi.js';
import * as pu from "./pixiUtils";


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
    alpha: 1,
  });

  sprite.interactive = true;


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
    strokeWidth: 2,
    strokeColor: isInSelectionArea.lift(isSelected,
      (isInSelectionAreaV: boolean, isSelectedV: boolean): number =>
        isInSelectionAreaV ? 0xcd0000 : isSelectedV ? 0xe3fc03 : 0x87cefa
    ),
  });

  container.addChild(frameRectangle);

  const hoverRectangle = pu.graphicsRectangle({
    x: x.map(x => x - 2),
    y: y.map(y => y - 2),
    width: width.map(w => w + 4),
    height: height.map(h => h + 4),
    strokeWidth: 2,
    strokeColor: isHovered.map<number>(h => {
      // console.log(`h: ${h}`);
      return h ? 0x0000cd : 0xFFFFFF;
    }),
  });

  container.addChild(hoverRectangle);

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
