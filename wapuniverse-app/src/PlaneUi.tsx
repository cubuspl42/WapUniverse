import './Editor.css';
import {Plane} from './editor/Plane';
import {edObjectSprite} from "./EdObjectUi";
import * as frp from "./frp/Set";
import {Container, Node} from "./renderer/Renderer";
import {SceneResources} from './SceneResources';
import {tileChunk, tileSprite} from "./TileUi";

export const chunkSize = 128;

export const planeNode = (
  res: SceneResources,
  plane: Plane,
) => {
  const rootChildren = new Set<Node>();

  const chunkMapWidth = Math.ceil(plane.tiles.width / chunkSize);
  const chunkMapHeight = Math.ceil(plane.tiles.height / chunkSize);

  for (let i = 0; i < chunkMapHeight; ++i) {
    for (let j = 0; j < chunkMapWidth; ++j) {
      tileChunk(plane, res, i, j, rootChildren);
      // sprites.forEach((s) => rootChildren.add(s));
    }
  }

  // plane.tiles.forEachIndexed((i, j, t) => {
  //   // if (j > 100 || j > 100) return;
  //
  //   rootChildren.add(tileSprite(plane, res, i, j, t));
  // });

  console.log(`rootChildren.size: ${rootChildren.size}`);

  plane.objects.forEach((o) => {
    rootChildren.add(edObjectSprite(res, o));
  });

  const root = new Container({
    children: frp.Set.hold(rootChildren),
  });

  return root;
};
