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
  tint?: Cell<number> | number;
}

export function sprite(params: SpriteParams): PIXI.Sprite {
  const sprite = new PIXI.Sprite();

  if (params.x instanceof Cell) {
    params.x.forEach((x) => sprite.x = x);
  } else if (params.x !== undefined) {
    sprite.x = params.x;
  }

  if (params.y instanceof Cell) {
    params.y.forEach((y) => sprite.y = y);
  } else if (params.y !== undefined) {
    sprite.y = params.y;
  }

  if (params.texture instanceof Cell) {
    params.texture.forEach((texture) => sprite.texture = texture);
  } else if (params.texture !== undefined) {
    sprite.texture = params.texture;
  }

  if (params.alpha instanceof Cell) {
    params.alpha.forEach((alpha) => sprite.alpha = alpha);
  } else if (params.alpha !== undefined) {
    sprite.alpha = params.alpha;
  }

  if (params.tint instanceof Cell) {
    params.tint.forEach((tint) => {
      // console.log(`tint!`);
      sprite.tint = tint;
    });
  } else if (params.tint !== undefined) {
    sprite.tint = params.tint;
  }

  return sprite;
}

export interface GraphicsRectangleParams {
  x: Cell<number> | number;
  y: Cell<number> | number;
  width: Cell<number> | number;
  height: Cell<number> | number;
  strokeWidth: Cell<number> | number;
  strokeColor: Cell<number> | number;
}

export function graphicsRectangle(params: GraphicsRectangleParams): PIXI.DisplayObject {
  const graphics = new PIXI.Graphics();

  function redraw() {
    graphics.clear();
    graphics.lineStyle(
      params.strokeWidth instanceof Cell ?
        params.strokeWidth.sample() : params.strokeWidth,
      0xFFFFFF,
    );
    graphics.drawRect(
      params.x instanceof Cell ?
        params.x.sample() : params.x,
      params.y instanceof Cell ?
        params.y.sample() : params.y,
      params.width instanceof Cell ?
        params.width.sample() : params.width,
      params.height instanceof Cell ?
        params.height.sample() : params.height,
    );
    graphics.endFill();
  }

  if (params.x instanceof Cell) {
    params.x.listen(() => redraw());
  }
  if (params.y instanceof Cell) {
    params.y.listen(() => redraw());
  }
  if (params.width instanceof Cell) {
    params.width.listen(() => redraw());
  }
  if (params.height instanceof Cell) {
    params.height.listen(() => redraw());
  }
  if (params.strokeWidth instanceof Cell) {
    params.strokeWidth.listen(() => redraw());
  }

  if (params.strokeColor instanceof Cell) {
    params.strokeColor.forEach((strokeColor) => {
      graphics.tint = strokeColor;
    });
  } else if (params.strokeColor !== undefined) {
    graphics.tint = params.strokeColor;
  }

  redraw();

  return graphics;
}
