import {fetchRezIndex, Image, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {Cell, CellSink} from "sodiumjs";
import * as PIXI from 'pixi.js';
import {Vec2} from "./Vec2";

type Texture = PIXI.Texture;

class Rectangle {
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

export class EdObject {
  readonly position: Cell<Vec2>;

  readonly texture: Cell<Texture>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  readonly isInSelectionArea: Cell<boolean>;

  constructor(
    rezIndex: RezIndex,
    levelResources: LevelResources,
    areaSelection: Cell<AreaSelection | null>,
    initialPosition: Vec2,
    initialImageSet: string
  ) {
    function getRezImage(imageSetId: string, i: number): Image {
      const rezImageSet = rezIndex.imageSets[imageSetId];
      const pidFileName = rezImageSet.frames[i];
      return rezImageSet.sprites[pidFileName];
    }

    function getTexture(rezImage: Image) {
      return levelResources.getTexture(rezImage.path);
    }

    function calculateBoundingBox(position: Vec2, rezImage: Image, texture: Texture): Rectangle {
      const [offsetX, offsetY] = rezImage.offset; // FIXME: Position means center
      return new Rectangle(position.x, position.y, texture.width, texture.height);
    }

    const position = new CellSink(initialPosition);
    const i = new CellSink(-1);
    const imageSet = new CellSink(initialImageSet);

    const rezImage = imageSet.lift(i, getRezImage);
    const texture = rezImage.map(getTexture);
    const boundingBox = position.lift3(rezImage, texture, calculateBoundingBox);

    const isInSelectionArea = Cell.switchC(areaSelection.map(a => {
      return a !== null ?
        a.objectsInArea.map(o => o.indexOf(this) !== -1) :
        new Cell(false);
    }));

    this.position = position;
    this.texture = texture;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
  }
}

export class App {
  editor = Editor.create();
}

export class AreaSelection {
  readonly rectangle: Cell<Rectangle>;

  readonly objectsInArea: Cell<ReadonlyArray<EdObject>>;

  private readonly _onDone: () => void;

  constructor(
    origin: Vec2,
    destination: Cell<Vec2>,
    objects: ReadonlyArray<EdObject>, onDone: () => void
  ) {
    this._onDone = onDone;
    const area = destination.map(d => {
      return new Rectangle(
        origin.x, origin.y, d.x - origin.x, d.y - origin.y);
    });
    this.rectangle = area;
    this.objectsInArea = area.map((area) => {
      return objects.filter((object) => {
        const boundingBox = object.boundingBox.sample();
        return area.overlaps(boundingBox);
      }) as ReadonlyArray<EdObject>;
    });
  }

  commit() {
    this._onDone();
  }
}

export class Editor {
  readonly objects: ReadonlyArray<EdObject>;

  private _areaSelection = new CellSink<AreaSelection | null>(null);

  readonly areaSelection = this._areaSelection as Cell<AreaSelection | null>;

  private constructor(rezIndex: RezIndex, levelResources: LevelResources) {
    this.objects = [
      new EdObject(
        rezIndex, levelResources, this.areaSelection,
        new Vec2(64, 64),
        "LEVEL1_IMAGES_OFFICER"
      ),
      new EdObject(
        rezIndex, levelResources, this.areaSelection,
        new Vec2(256, 256),
        "LEVEL1_IMAGES_SKULL"
      )
    ];
  }

  static async create(): Promise<Editor> {
    const rezIndex = await fetchRezIndex();
    const resources = await LevelResources.load(rezIndex, 1);
    return new Editor(rezIndex, resources);
  }

  selectByArea(origin: Vec2, destination: Cell<Vec2>) {
    const areaSelection = new AreaSelection(origin, destination, this.objects, () => {
      this._areaSelection.send(null);
    });
    this._areaSelection.send(areaSelection);
  }
}
