import { LevelResources } from "./LevelResources";
import { Node, Nothing, Sprite } from "./renderer/Renderer";
import { SceneResources } from "./SceneResources";

const tileWidth = 64;
const tileHeight = 64;

function pad(n: number, width: number) {
  const nStr = n + '';
  return nStr.length >= width ?
    nStr : new Array(width - nStr.length + 1).join('0') + nStr;
}

export function tileSprite(
  res: SceneResources,
  i: number,
  j: number,
  tileId: number,
): Node {
  const texture = res.getGameImage(`LEVEL14/TILES/ACTION/${pad(tileId, 3)}.PID`);

  return new Sprite({
    x: j * tileWidth,
    y: i * tileHeight,
    texture: texture,
  });
}
