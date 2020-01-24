import {Vec2} from "./Vec2";
import {Image, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {AreaSelection} from "./AreaSelection";
import {Rectangle} from "./Rectangle";
import {EditorInternal} from "./Editor";
import {Maybe, None, Some} from "./Maybe";
import {Cell, CellSink} from "./frp";
import {Texture} from "./renderer/Renderer";

interface ImageData {
  readonly imageSetId: String;
  readonly rezImage: Image;
  readonly texture: Texture;
}

export function getRandomInt(max: number) {
  let min = 0;
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export class EdObject {
  readonly _editor: EditorInternal;

  readonly position: Cell<Vec2>;

  readonly i: CellSink<number>;

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
    areaSelection: Cell<Maybe<AreaSelection>>,
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

    const position = new CellSink(initialPosition);
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
    // const shortImageSetId = new CellSink("GAME_EXTRALIFE");

    const imageSetId = shortImageSetId.map((s) => editor.expandShortImageSetId(s));

    const imageData = imageSetId.lift(i,
      (isM, i) => isM
        .flatMap(is => getImageData(is, i))
        .orElse(() => getImageData("GAME_IMAGES_POWERUPS_EXTRALIFE", -1).get()));

    const texture = imageData.map((id) => id.texture);

    const boundingBox = position.lift(imageData, (p: Vec2, id: ImageData) =>
      calculateBoundingBox(p, id.rezImage, id.texture));

    const falseCell = new CellSink<boolean>(false);

    const isInSelectionArea = areaSelection.flatMap(aM => aM.map((a) =>
      a.objectsInArea
        .map(o => o.has(this)))
      .orElse(() => falseCell)
    );

    const isSelected = editor.selectedObjects.map(s => s.has(this));

    this._editor = editor;
    this.position = position;
    this.i = i;
    this.texture = texture;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
    this.isSelected = isSelected;
    this.id = id;
  }
}
