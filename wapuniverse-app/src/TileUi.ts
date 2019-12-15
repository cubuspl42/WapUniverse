import {LevelResources} from "./LevelResources";
import {Node, Nothing, Sprite} from "./renderer/Renderer";

const tileWidth = 64;
const tileHeight = 64;

function pad(n: number, width: number) {
  const nStr = n + '';
  return nStr.length >= width ?
    nStr : new Array(width - nStr.length + 1).join('0') + nStr;
}

export function tileSprite(
  levelResources: LevelResources,
  i: number,
  j: number,
  tileId: number,
): Node {
  const texture = levelResources.getTexture(`LEVEL14/TILES/ACTION/${pad(tileId, 3)}.PID`);

  const displayObject = texture
    .map((texture) => {
      const sprite = new Sprite({
        x: j * tileWidth,
        y: i * tileHeight,
        texture: texture,
      });
      return sprite as Node;
    })
    .orElse(() => new Nothing());

  return displayObject;
}
