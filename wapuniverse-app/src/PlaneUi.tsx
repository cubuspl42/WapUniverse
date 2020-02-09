import './Editor.css';
import { Plane } from './editor/Plane';
import { edObjectSprite } from "./EdObjectUi";
import * as frp from "./frp/Set";
import { Container, Node } from "./renderer/Renderer";
import { SceneResources } from './SceneResources';
import { tileSprite } from "./TileUi";

export const planeNode = (
  res: SceneResources,
  plane: Plane,
) => {
  const rootChildren = new Set<Node>();
  plane.tiles.forEachIndexed((i, j, t) => {
    rootChildren.add(tileSprite(plane.world, res, i, j, t));
  });

  plane.objects.forEach((o) => {
    rootChildren.add(edObjectSprite(res, o));
  });

  const root = new Container({
    children: new frp.Set(rootChildren),
  });

  return root;
};
