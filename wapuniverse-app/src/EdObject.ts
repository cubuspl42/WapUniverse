import {Cell, CellSink} from "sodiumjs";
import {Vec2} from "./Vec2";
import {Image, RezIndex} from "./rezIndex";
import {LevelResources} from "./LevelResources";
import {AreaSelection} from "./AreaSelection";
import {Rectangle} from "./Rectangle";
import {EditorInternal} from "./Editor";

type Texture = PIXI.Texture;

export class EdObject {
  readonly _editor: EditorInternal;

  readonly position: Cell<Vec2>;

  readonly texture: Cell<Texture>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  readonly isInSelectionArea: Cell<boolean>;

  readonly isSelected: Cell<boolean>;

  constructor(
    editor: EditorInternal,
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

    const isSelected = editor.selectedObjects.map(s => s.indexOf(this) !== -1);

    this._editor = editor;
    this.position = position;
    this.texture = texture;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
    this.isSelected = isSelected;
  }
}
