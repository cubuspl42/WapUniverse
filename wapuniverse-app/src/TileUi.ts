import { LevelResources } from "./LevelResources";
import { Node, Nothing, Sprite } from "./renderer/Renderer";
import { SceneResources } from "./SceneResources";
import { Editor } from "./Editor";

const tileWidth = 64;
const tileHeight = 64;

function pad(n: number, width: number) {
  const nStr = n + '';
  return nStr.length >= width ?
    nStr : new Array(width - nStr.length + 1).join('0') + nStr;
}

export function tileSprite(
  editor: Editor,
  res: SceneResources,
  i: number,
  j: number,
  tileId: number,
): Node {
  const rezImage = editor.getTileRezImage(tileId);
  const texture = rezImage.flatMap((ri) => res.getTexture(ri.path));

  return new Sprite({
    x: j * tileWidth,
    y: i * tileHeight,
    texture: texture,
  });
}
