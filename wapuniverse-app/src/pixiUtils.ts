import * as PIXI from "pixi.js";
import {Cell} from "./Cell";

export function autoResizingPixiApplication(parent: HTMLElement) {
  const application = new PIXI.Application({
    resizeTo: parent,
  });

  parent.appendChild(application.view);

  return application;
}

export interface SpriteParams {
  x: Cell<number> | number;
  y: Cell<number> | number;
  texture: Cell<PIXI.Texture> | PIXI.Texture;
  alpha: Cell<number> | number;
}

export function sprite(params: SpriteParams): PIXI.Sprite {
  const sprite = new PIXI.Sprite();

  if(params.x instanceof Cell) {
    params.x.forEach((x) => sprite.x = x);
  } else {
    sprite.x = params.x;
  }

  if(params.y instanceof Cell) {
    params.y.forEach((y) => sprite.y = y);
  } else {
    sprite.y = params.y;
  }

  if(params.texture instanceof Cell) {
    params.texture.forEach((texture) => sprite.texture = texture);
  } else {
    sprite.texture = params.texture;
  }

  if(params.alpha instanceof Cell) {
    params.alpha.forEach((alpha) => sprite.alpha = alpha);
  } else {
    sprite.alpha = params.alpha;
  }
  
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

  redraw();

  return graphics;
}
