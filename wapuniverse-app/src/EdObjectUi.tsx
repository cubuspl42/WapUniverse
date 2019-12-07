import './Editor.css';
import {EdObject} from "./EdObject";
import * as PIXI from 'pixi.js';
import * as pu from "./pixiUtils";

export function edObjectSprite(object: EdObject):
  PIXI.DisplayObject {
  const boundingBox = object.boundingBox;
  const x = boundingBox.map((b) => b.xMin);
  const y = boundingBox.map((b) => b.yMin);
  const texture = object.texture;
  const isHovered = object.isHovered;

  const sprite = pu.sprite({
    x: x,
    y: y,
    texture: texture,
    alpha: isHovered.map<number>(h => h ? 1 : 0.5),
  });

  sprite.interactive = true;

  sprite.on("pointerover", () => {
    object.isHovered.send(true);
  });

  sprite.on("pointerout", () => {
    object.isHovered.send(false);
  });

  return sprite;
}

export function edObjectBorder(
  object: EdObject,
  borderTexture: PIXI.Texture,
): PIXI.DisplayObject {
  const boundingBox = object.boundingBox;
  const x = boundingBox.map((b) => b.xMin);
  const y = boundingBox.map((b) => b.yMin);
  const width = boundingBox.map((b) => b.width);
  const height = boundingBox.map((b) => b.height);

  const border = new PIXI.NineSlicePlane(
    borderTexture,
    3,
    3,
    3,
    3,
  );

  border.x = x.sample();
  border.y = y.sample();
  border.width = width.sample();
  border.height = height.sample();
  border.tint = 0xee0000;

  return border;
}

