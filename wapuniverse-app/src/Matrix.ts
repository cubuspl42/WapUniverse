export class Matrix<T> {
  readonly width: number;
  readonly height: number;
  private readonly array: readonly T[];

  constructor(
    width: number,
    height: number,
    array: readonly T[],
  ) {
    if (array.length != width * height)
      throw Error("Invalid array length");
    this.width = width;
    this.height = height;
    this.array = array;
  }

  get(i: number, j: number): T {
    return this.array[i * this.width + j];
  }

  forEach(f: (t: T) => void) {
    this.forEachIndexed((_1, _2, t: T) => f(t));
  }

  forEachIndexed(f: (i: number, j: number, t: T) => void) {
    for (let i = 0; i < this.height; ++i) {
      for (let j = 0; j < this.width; ++j) {
        f(i, j, this.get(i, j));
      }
    }
  }
}
