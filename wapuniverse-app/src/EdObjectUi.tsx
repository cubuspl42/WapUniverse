import './Editor.css';
import {EdObject} from "./editor/EdObject";
import * as PIXI from 'pixi.js';
import {Node, Sprite} from "./renderer/Renderer";
import {SceneResources} from './SceneResources';
import {Maybe} from './Maybe';
import {Vec2} from './Vec2';
import {StreamSink, Unit} from "./sodium";

export function edObjectSprite(
  res: SceneResources,
  object: EdObject,
): Node {
  const boundingBox = object.boundingBox;
  // const x = boundingBox.map((b) => b.xMin);
  // const y = boundingBox.map((b) => b.yMin);

  const x = object.correctedPosition.map((p) => p.x);
  const y = object.correctedPosition.map((p) => p.y);
  const texture = object.image.map((i) => res.getTexture(i.pidPath));
  const isHovered = object.isHovered;

  const scaleX = object.isMirrored ? -1 : 1;
  const scaleY = object.isInverted ? -1 : 1;

  const sprite = new Sprite({
    x: x,
    y: y,
    texture: texture,
    // alpha: isHovered.map<number>(h => h ? 1 : 0.5),
    outline: object.isInSelectionArea,
    pivot: object.image.map((gi) => gi.size.div(2)),
    scale: new Vec2(scaleX, scaleY),
    visible: object.isVisible,
    interactive: true,
  });


  const onPointerDown = new StreamSink<Unit>();
  object.selectLate.lateLoop(onPointerDown);
  sprite.onPointerDown(() => {
    onPointerDown.send(Unit.UNIT);
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

