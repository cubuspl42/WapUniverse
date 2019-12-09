import * as PIXI from 'pixi.js';

export class Vec2 {
  readonly x: number;
  readonly y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  add(other: Vec2): Vec2 {
    return new Vec2(
      this.x + other.x,
      this.y + other.y,
    );
  }

  sub(other: Vec2): Vec2 {
    return new Vec2(
      this.x - other.x,
      this.y - other.y,
    );
  }

  div(a: number) {
    return new Vec2(
      this.x / a,
      this.y / a,
    );
  }

  neg() {
    return new Vec2(
      -this.x,
      -this.y,
    );
  }

  floor() {
    return new Vec2(
      Math.floor(this.x),
      Math.floor(this.y),
    );
  }

  toString(): string {
    return `(${this.x}, ${this.y})`;
  }

  toPixiPoint(): PIXI.Point {
    return new PIXI.Point(this.x, this.y);
  }
}
