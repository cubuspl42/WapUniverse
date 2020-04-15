import * as PIXI from "pixi.js";
import * as frp from "../frp/Set";
import {Cell} from "sodium";
import {Maybe} from "../Maybe";
import {OutlineFilter} from '@pixi/filter-outline';
import {Vec2} from "../Vec2";

function link<T>(cell: Cell<T> | T | undefined, set: (value: T) => void): void {
  if (cell instanceof Cell) {
    cell.forEach(set);
  } else if (cell !== undefined) {
    set(cell); // cell is T
  }
}

function linkMaybe<T>(cell: Cell<Maybe<T>> | Maybe<T> | undefined, set: (value: T | null) => void): void {
  link(cell, (v) => set(
    v.map((t) => <T | null>t)
      .orElse(() => null)
  ));
}

interface ContextProps {
  parent: HTMLElement;
}

export class Context {
  readonly _stage: PIXI.Container;

  constructor({parent}: ContextProps) {
    const pixiApplication = new PIXI.Application({
      resizeTo: parent,
    });

    parent.appendChild(pixiApplication.view);

    this._stage = pixiApplication.stage;
  }

  setRoot(root: Node) {
    this._stage.removeChildren();
    this._stage.addChild(root._displayObject);
  }
}

export abstract class Node {
  abstract readonly _displayObject: PIXI.DisplayObject;

  abstract dispose(): void;
}

interface TextureProps {
  context: Context;
  src: string;
}

export class Texture {
  readonly _pixiTexture: PIXI.Texture;

  // private readonly context: Context;

  // private
  constructor(
    // context: Context,
    pixiTexture: PIXI.Texture,
  ) {
    this._pixiTexture = pixiTexture;
    // this.context = context;
  }

  static load(
    // context: Context,
    src: string,
  ): Texture {
    const pixiTexture = PIXI.Texture.from(src);
    return new Texture(
      // context,
      pixiTexture,
    );
  }

  static fromImage(img: HTMLImageElement): Texture {
    const baseTexture = PIXI.BaseTexture.from(img);
    const pixiTexture = new PIXI.Texture(baseTexture);
    return new Texture(pixiTexture);
  }

  get width(): number {
    return this._pixiTexture.width;
  }

  get height(): number {
    return this._pixiTexture.height;
  }

  dispose(): void {
    this._pixiTexture.destroy();
  }
}

export class TextureArray {

}

export interface SpriteProps {
  context: Context;
  texture: Texture | null;
}

export interface SpriteParams {
  x: Cell<number> | number;
  y: Cell<number> | number;
  pivot?: Cell<Vec2> | Vec2;
  texture: Cell<Maybe<Texture>> | Maybe<Texture>;
  alpha?: Cell<number> | number;
  tint?: Cell<number> | number;
  outline?: Cell<boolean> | boolean;
  scale?: Cell<Vec2> | Vec2;
  visible?: Cell<boolean> | boolean;
  interactive?: boolean;
}

export class Sprite extends Node {
  readonly _displayObject: PIXI.Sprite;

  constructor(params: SpriteParams) {
    super();

    const sprite = new PIXI.Sprite();
    const spriteAny = sprite as any;
    const outlineFilter = new OutlineFilter(3, 0xff0000);
    outlineFilter.padding = 6;

    link(params.x, (v) => sprite.x = v);
    link(params.y, (v) => sprite.y = v);
    link(params.pivot, (v) => sprite.pivot = v.toPixiPoint());
    linkMaybe(params.texture, (v) => spriteAny.texture = v && v._pixiTexture);
    link(params.alpha, (v) => sprite.alpha = v);
    link(params.tint, (v) => spriteAny.tint = v);
    link(params.outline, (v) => spriteAny.filters = v ? [outlineFilter] : []);
    link(params.scale, (v) => sprite.scale = v.toPixiPoint());
    link(params.visible, (v) => sprite.visible = v);

    if (params.interactive !== undefined) sprite.interactive = params.interactive;

    this._displayObject = sprite;
  }

  onPointerDown(callback: () => void) {
    this._displayObject.on("pointerdown", callback);
  }

  onPointerOver(callback: () => void) {
    this._displayObject.on("pointerover", callback);
  }

  onPointerOut(callback: () => void) {
    this._displayObject.on("pointerout", callback);
  }

  dispose(): void {
  }
}

export interface GraphicRectangleParams {
  x: Cell<number>;
  y: Cell<number>;
  width: Cell<number>;
  height: Cell<number>;
}

export class GraphicRectangle extends Node {
  readonly _displayObject: PIXI.DisplayObject;

  constructor(params: GraphicRectangleParams) {
    super();

    const graphics = new PIXI.Graphics;
    graphics.alpha = 0.5;

    link(params.x, (v) => graphics.x = v);
    link(params.y, (v) => graphics.y = v);

    params.width
      .lift(params.height, (w, h): [number, number] => [w, h])
      .listen(([w, h]) => {
        graphics.clear();
        graphics.beginFill(0xFFFF00);
        graphics.lineStyle(5, 0xFF0000);
        graphics.drawRect(0, 0, w, h);
        // console.log('GraphicRectangle.listen', {w, h});
      });

    this._displayObject = graphics;
    // this._displayObject = new PIXI.Container();
  }

  dispose(): void {
  }
}


export interface ContainerParams {
  x?: Cell<number> | number;
  y?: Cell<number> | number;
  pivot?: Cell<PIXI.Point> | PIXI.Point;
  scale?: Cell<PIXI.Point> | PIXI.Point;
  children: frp.Set<Node>;
}

export class Container extends Node {
  constructor(params: ContainerParams) {
    console.log("Container constructor");
    super();

    const pixiContainer = new PIXI.Container();

    link(params.x, (v) => pixiContainer.x = v);
    link(params.y, (v) => pixiContainer.y = v);
    link(params.pivot, (v) => pixiContainer.pivot = v);
    link(params.scale, (v) => pixiContainer.scale = v);

    params.children.cell.forEach((nodes) => {
      pixiContainer.removeChildren();
      nodes.forEach((node) => pixiContainer.addChild(node._displayObject));
    });

    this._displayObject = pixiContainer;
  }

  readonly _displayObject: PIXI.DisplayObject;

  dispose(): void {
  }
}

export class Nothing extends Node {
  readonly _displayObject = new PIXI.Container();

  dispose(): void {
  }
}

// export class SpriteMatrix extends Node {
//   private parent: PIXI.Container | null = null;
//
//   private container: PIXI.Container | null = null;
//
//   private map = new Map<Vec2, PIXI.Sprite>();
//
//
//   _init(parent: PIXI.Container): void {
//
//     this.parent = parent;
//   }
//
//   _dispose(): void {
//   }
// }


// export interface SceneProps {
//   context: Context;
// }
//
// export class Scene {
//   private stage: PIXI.Container;
//
//   constructor({context}: SceneProps, root: Node) {
//     const stage = context._stage;
//     stage = root._displayObject;
//   }
//
//   // addChild(node: Node): void {
//   //   this.nodes.add(node);
//   //   this.stage.addChild()
//   // }
//   //
//   // removeChild(node: Node): void {
//   //   this.nodes.delete(node);
//   //   this.stage.removeChild(node._displayObject);
//   // }
// }

// export interface RendererProps {
//   scene: Scene;
//   parent: HTMLElement;
// }

// export class Renderer {
//   private readonly scene: Scene;
//   // private readonly gl: WebGLRenderingContext;
//
//   readonly view: HTMLCanvasElement;
//
//   constructor({
//                 scene,
//                 parent,
//               }: RendererProps) {
//     // const canvas = document.createElement('canvas');
//
//     // const gl = canvas.getContext("webgl2")! as WebGLRenderingContext;
//
//     // new ResizeObserver(() => {
//     //   canvas.width = parent.offsetWidth;
//     //   canvas.height = parent.offsetHeight;
//     // }).observe(parent);
//
//     const pixiApplication = new PIXI.Application({
//       resizeTo: parent,
//     });
//
//     parent.appendChild(pixiApplication.view);
//
//     scene._init(pixiApplication.stage);
//
//     this.scene = scene;
//     // this.gl = gl;
//     this.view = pixiApplication.view;
//   }
// }
