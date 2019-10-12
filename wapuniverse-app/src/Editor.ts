import {fetchRezIndex, Image, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {Cell, CellSink} from "sodiumjs";
import * as PIXI from 'pixi.js';

type Texture = PIXI.Texture;
type Rectangle = PIXI.Rectangle;

const Rectangle = PIXI.Rectangle;

class Vec2 {
  readonly x: number;
  readonly y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }
}

export class EdObject {
  readonly position: Cell<Vec2>;

  readonly texture: Cell<Texture>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  constructor(
    rezIndex: RezIndex,
    levelResources: LevelResources,
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

    this.position = position;
    this.texture = texture;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
  }

}

export class App {
  editor = Editor.create();
}

export class Editor {
  readonly objects: ReadonlyArray<EdObject>;

  private constructor(rezIndex: RezIndex, levelResources: LevelResources) {
    this.objects = [
      new EdObject(
        rezIndex, levelResources,
        new Vec2(64, 64),
        "LEVEL1_IMAGES_OFFICER"
      ),
      new EdObject(
        rezIndex, levelResources,
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
}
