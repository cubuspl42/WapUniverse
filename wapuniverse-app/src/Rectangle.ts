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

  constructor(xMin: number, yMin: number, width: number, height: number) {
    // if (width < 0) {
    //   throw new Error('`width` must be >= 0');
    // }
    // if (height < 0) {
    //   throw new Error('`height` must be >= 0');
    // }
    this.xMin = xMin;
    this.yMin = yMin;
    this.width = width;
    this.height = height;
  }

  overlaps(b: Rectangle): boolean {
    return this.xMin < b.xMax && b.xMin < this.xMax &&
      this.yMin < b.yMax && b.yMin < this.yMax;
  }

  toString() {
    return `(xMin: ${this.xMin}, yMin: ${this.yMin}, width: ${this.width}, height: ${this.height}, xMax: ${this.xMax}, yMax: ${this.yMax}`;
  }
}
