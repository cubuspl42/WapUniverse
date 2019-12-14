import * as PIXI from 'pixi.js';
import {LevelResources} from "./LevelResources";

const tileWidth = 64;
const tileHeight = 64;

export function tileSprite(
  levelResources: LevelResources,
  i: number,
  j: number,
  tileId: number,
): PIXI.DisplayObject {
  const texture = levelResources.getTexture("LEVEL14/TILES/ACTION/007.PID");

  const displayObject = texture
    .map((texture) => {
      const sprite = new PIXI.Sprite(texture);
      sprite.x = j * tileWidth;
      sprite.y = i * tileHeight;
      return sprite as PIXI.DisplayObject;
    })
    .orElse(() => new PIXI.Container());

  return displayObject;
}
