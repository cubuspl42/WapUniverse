import {Cell, CellSink} from "sodiumjs";
import {Vec2} from "./Vec2";
import {Image, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {AreaSelection} from "./AreaSelection";
import {Rectangle} from "./Rectangle";
import {EditorInternal} from "./Editor";
import {Maybe, None, Some} from "./Maybe";

type Texture = PIXI.Texture;

interface ImageData {
  readonly imageSetId: String;
  readonly rezImage: Image;
  readonly texture: Texture;
}

export class EdObject {
  readonly _editor: EditorInternal;

  readonly position: Cell<Vec2>;

  readonly texture: Cell<Texture>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  readonly isInSelectionArea: Cell<boolean>;

  readonly isSelected: Cell<boolean>;

  readonly id: number;

  constructor(
    editor: EditorInternal,
    rezIndex: RezIndex,
    levelResources: LevelResources,
    areaSelection: Cell<AreaSelection | null>,
    initialPosition: Vec2,
    initialImageSet: string,
    id: number,
  ) {
    function getRezImage(imageSetId: string, i: number): Maybe<Image> {
      const rezImageSet = rezIndex.imageSets[imageSetId];
      if (!rezImageSet) return new None();
      const pidFileName = rezImageSet.frames[i];
      if (!pidFileName) return new None();
      return new Some(rezImageSet.sprites[pidFileName]);
    }

    function getTexture(rezImage: Image): Maybe<Texture> {
      return levelResources.getTexture(rezImage.path);
    }

    function calculateBoundingBox(position: Vec2, rezImage: Image, texture: Texture): Rectangle {
      const [offsetX, offsetY] = rezImage.offset; // FIXME: Position means center
      return new Rectangle(position.x, position.y, texture.width, texture.height);
    }

    function getRandomInt(max: number) {
      let min = 0;
      min = Math.ceil(min);
      max = Math.floor(max);
      return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    // const position = new CellSink(initialPosition);
    const position = new CellSink(new Vec2(getRandomInt(800), getRandomInt(600)));
    const i = new CellSink(-1);

    function getImageData(imageSetId: string, i: number): Maybe<ImageData> {
      return getRezImage(imageSetId, i).flatMap(
        (rezImage) => getTexture(rezImage).map(
          (texture) => {
            return {
              imageSetId: imageSetId,
              rezImage: rezImage,
              texture: texture,
            };
          },
        ),
      );
    }

    const shortImageSetId = new CellSink(initialImageSet);

    const imageSetId = shortImageSetId.map((s) => editor.expandShortImageSetId(s));

    const imageData = imageSetId.lift(i,
      (isM, i) => isM
        .flatMap(is => getImageData(is, i))
        .orElse(() => getImageData("LEVEL1_IMAGES_OFFICER", -1).get()));

    const texture = imageData.map((id) => id.texture);

    const boundingBox = position.lift(imageData, (p: Vec2, id: ImageData) =>
      calculateBoundingBox(p, id.rezImage, id.texture));

    const isInSelectionArea = Cell.switchC(areaSelection.map(a => {
      return a !== null ?
        a.objectsInArea.map(o => o.indexOf(this) !== -1) :
        new Cell(false);
    }));

    const isSelected = editor.selectedObjects.map(s => s.indexOf(this) !== -1);

    this._editor = editor;
    this.position = position;
    this.texture = texture;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
    this.isSelected = isSelected;
    this.id = id;
  }
}
