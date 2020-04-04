import {LevelResources} from "./LevelResources";
import {Container, Node, Nothing, Sprite} from "./renderer/Renderer";
import {SceneResources} from "./SceneResources";
import {Editor} from "./editor/Editor";
import {World} from "./editor/World";
import {Plane} from "./editor/Plane";
import {Rectangle} from "./Rectangle";
import {Vec2} from "./Vec2";
import {chunkSize} from "./PlaneUi";
import {Cell} from "./sodium";

const tileWidth = 64;
const tileHeight = 64;
const tileSize = new Vec2(tileWidth, tileHeight);

function pad(n: number, width: number) {
  const nStr = n + '';
  return nStr.length >= width ?
    nStr : new Array(width - nStr.length + 1).join('0') + nStr;
}

export function tileSprite(
  plane: Plane,
  res: SceneResources,
  i: number,
  j: number,
  tileId: number,
  isVisible: Cell<boolean>,
): Sprite {
  const rezImage = plane.getTileRezImage(tileId);
  const texture = rezImage.flatMap((ri) => res.getTexture(ri.path));

  const position = new Vec2(j * tileWidth, i * tileHeight);

  return new Sprite({
    x: position.x,
    y: position.y,
    texture: texture,
    visible: isVisible,
  });
}

export function tileChunk(
  plane: Plane,
  res: SceneResources,
  ci: number,
  cj: number,
  children: Set<Node>,
): void {
  const tiles = plane.tiles;

  const i0 = ci * chunkSize;
  const j0 = cj * chunkSize;

  const rect = new Rectangle(
    new Vec2(j0 * tileWidth, i0 * tileHeight),
    new Vec2(chunkSize * tileWidth, chunkSize * tileHeight),
  );

  const isVisible: Cell<boolean> = plane.editor.windowRect.map((wr) => wr.overlaps(rect));
  // const isVisible = new Cell<boolean>(false)


  for (let i = 0; i < chunkSize; ++i) {
    for (let j = 0; j < chunkSize; ++j) {
      const ti = i0 + i;
      const tj = j0 + j;
      const t = tiles.get(ti, tj);

      if (ti < tiles.height && tj < tiles.width && t > 0) {
        children.add(tileSprite(plane, res, ti, tj, t, isVisible));
      }
    }
  }
}
