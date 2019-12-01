import * as PIXI from "pixi.js";
import {Cell} from "sodiumjs";

export function autoResizingPixiApplication(parent: HTMLElement) {
  const application = new PIXI.Application({
    resizeTo: parent,
  });

  parent.appendChild(application.view);

  return application;
}

export interface SpriteParams {
  x: Cell<number>;
  y: Cell<number>;
  texture: Cell<PIXI.Texture>;
  alpha: Cell<number>;
}

export function sprite(params: SpriteParams): PIXI.Sprite {
  const sprite = new PIXI.Sprite();
  params.x.listen((x) => sprite.x = x);
  params.y.listen((y) => sprite.y = y);
  params.texture.listen((t) => sprite.texture = t);
  params.alpha.listen((a) => sprite.alpha = a);
  return sprite;
}

export interface GraphicsRectangleParams {
  x: Cell<number>;
  y: Cell<number>;
  width: Cell<number>;
  height: Cell<number>;
  strokeWidth: Cell<number>;
  strokeColor: Cell<number>;
}

export function graphicsRectangle(params: GraphicsRectangleParams): PIXI.DisplayObject {
  const graphics = new PIXI.Graphics();

  function redraw() {
    graphics.clear();
    graphics.lineStyle(
      params.strokeWidth.sample(),
      params.strokeColor.sample(),
    );
    graphics.drawRect(
      params.x.sample(),
      params.y.sample(),
      params.width.sample(),
      params.height.sample(),
    );
    graphics.endFill();
  }

  params.strokeWidth.listen(() => redraw());
  params.strokeColor.listen(() => redraw());
  params.x.listen(() => redraw());
  params.y.listen(() => redraw());
  params.width.listen(() => redraw());
  params.height.listen(() => redraw());

  return graphics;
}
