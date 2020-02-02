import './Editor.css';
import { EdObject } from "./EdObject";
import * as PIXI from 'pixi.js';
import { Node, Sprite } from "./renderer/Renderer";
import { SceneResources } from './SceneResources';
import { Maybe } from './Maybe';

export function edObjectSprite(
  res: SceneResources,
  object: EdObject,
): Node {
  const boundingBox = object.boundingBox;
  const x = boundingBox.map((b) => b.xMin);
  const y = boundingBox.map((b) => b.yMin);
  const texture =  object.image.map((i) => res.getGameImage(i.pidPath));
  const isHovered = object.isHovered;

  const sprite = new Sprite({
    x: x,
    y: y,
    texture: texture,
    // alpha: isHovered.map<number>(h => h ? 1 : 0.5),
    tint: isHovered.map<number>(h => h ? 0xff0000 : 0xffffff),
    interactive: true,
  });

  sprite.onPointerOver(() => {
    object.isHovered.send(true);
  });

  sprite.onPointerOut(() => {
    object.isHovered.send(false);
  });

  return sprite;
}

// export function edObjectBorder(
//   object: EdObject,
//   borderTexture: PIXI.Texture,
// ): Node {
//   const boundingBox = object.boundingBox;
//   const x = boundingBox.map((b) => b.xMin);
//   const y = boundingBox.map((b) => b.yMin);
//   const width = boundingBox.map((b) => b.width);
//   const height = boundingBox.map((b) => b.height);
//
//   const border = new PIXI.NineSlicePlane(
//     borderTexture,
//     3,
//     3,
//     3,
//     3,
//   );
//
//   border.x = x.sample();
//   border.y = y.sample();
//   border.width = width.sample();
//   border.height = height.sample();
//   border.tint = 0xee0000;
//
//   return border;
// }

