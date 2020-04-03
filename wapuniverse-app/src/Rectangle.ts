import { Vec2 } from "./Vec2";

export class Rectangle {
  readonly xMin: number;
  readonly yMin: number;
  readonly width: number;
  readonly height: number;

  get xMax(): number {
    return this.xMin + this.width;
  }

  get yMax(): number {
    return this.yMin + this.height;
  }

  get xyMin(): Vec2 {
    return new Vec2(this.xMin, this.yMin);
  }

  get xyMax(): Vec2 {
    return new Vec2(this.xMax, this.yMax);
  }

  constructor(position: Vec2, size: Vec2) {
    // if (width < 0) {
    //   throw new Error('`width` must be >= 0');
    // }
    // if (height < 0) {
    //   throw new Error('`height` must be >= 0');
    // }
    this.xMin = position.x;
    this.yMin = position.y;
    this.width = size.width;
    this.height = size.height;
  }

  static fromBounds(left: number, top: number, right: number, bottom: number): Rectangle {
    return new this(new Vec2(left, top), new Vec2(right - left, bottom - top));
  }

  static fromDiagonal(a: Vec2, b: Vec2): Rectangle {
    return new this(new Vec2(Math.min(a.x, b.x), Math.min(a.y, b.y)), b.sub(a));
  }

  overlaps(b: Rectangle): boolean {
    return this.xMin < b.xMax && b.xMin < this.xMax &&
      this.yMin < b.yMax && b.yMin < this.yMax;
  }

  toString() {
    return `(xMin: ${this.xMin}, yMin: ${this.yMin}, width: ${this.width}, height: ${this.height}, xMax: ${this.xMax}, yMax: ${this.yMax})`;
  }

  map(f: (v: Vec2) => Vec2): Rectangle {
    return Rectangle.fromDiagonal(
      f(this.xyMin),
      f(this.xyMax),
    )
  }
}
