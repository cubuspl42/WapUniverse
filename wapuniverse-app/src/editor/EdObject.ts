import { LazyGetter } from 'lazy-get-decorator';
import { AreaSelection } from "../AreaSelection";
import { Cell, CellSink, LateStreamLoop } from "../frp";
import { GameImage, LevelResources } from "../LevelResources";
import { Maybe } from "../Maybe";
import { Rectangle } from "../Rectangle";
import { Texture } from "../renderer/Renderer";
import { RezImage, RezIndex } from "../rezIndex";
import { Vec2 } from "../Vec2";
import { DrawFlags, Object_ } from "../wwd";
import { Editor } from "./Editor";
import { Plane } from "./Plane";
import { World } from "./World";

interface ImageData {
  readonly imageSetId: String;
  readonly rezImage: RezImage;
  readonly texture: Texture;
}

export function getRandomInt(max: number) {
  let min = 0;
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export class EdObject {
  readonly plane: Plane;

  readonly wwdObject: Object_;

  readonly position: Cell<Vec2>;

  readonly correctedPosition: Cell<Vec2>;

  readonly i: CellSink<number>;

  readonly image: Cell<GameImage>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  readonly isInSelectionArea: Cell<boolean>;

  readonly id: number;

  readonly selectLate = new LateStreamLoop<void>();

  readonly select = this.selectLate.stream;

  readonly onSelected = this.select.map(() => this);

  get world(): World {
    return this.plane.world;
  }

  get editor(): Editor {
    return this.world.editor;
  }

  get isMirrored(): boolean {
    return (this.wwdObject.drawFlags & DrawFlags.Mirror) != 0;
  }

  get isInverted(): boolean {
    return (this.wwdObject.drawFlags & DrawFlags.Invert) != 0;
  }

  @LazyGetter()
  get isSelected(): Cell<boolean> {
    return this.editor.activePlaneEditor
      .flatMap((ape) => ape.selectedObject)
      .map((o) => o === this);
  }

  constructor(
    plane: Plane,
    rezIndex: RezIndex,
    levelResources: LevelResources,
    areaSelection: Cell<Maybe<AreaSelection>>,
    wwdObject: Object_,
    initialPosition: Vec2,
    initialImageSet: string,
    id: number,
  ) {
    const world = plane.world;
    const editor = plane.editor;

    function getGameImage(rezImage: RezImage): Maybe<GameImage> {
      return levelResources.getGameImage(rezImage.path);
    }

    const position = new CellSink(initialPosition);

    const i = new CellSink(wwdObject.i);

    function getImageData(imageSetId: string, i: number): Maybe<GameImage> {
      return world.getRezImage(imageSetId, i).flatMap((rezImage) => getGameImage(rezImage));
    }

    const shortImageSetId = new CellSink(initialImageSet);

    const imageSetId = shortImageSetId.map((s) => world.expandShortImageSetId(s));

    const image = imageSetId.lift(i,
      (isM, i) => isM
        .flatMap(is => getImageData(is, i))
        .orElse(() => getImageData("GAME_IMAGES_POWERUPS_EXTRALIFE", -1).get()));

    const correctedPosition = position.lift(image, (p, i) => p.add(i.offset));

    const boundingBox = correctedPosition.lift(image, (p: Vec2, gi: GameImage) =>
      new Rectangle(p.sub(gi.size.div(2)), gi.size));

    const falseCell = new CellSink<boolean>(false);

    const isInSelectionArea = areaSelection.flatMap(aM => aM.map((a) =>
      a.objectsInArea
        .map(o => o.has(this)))
      .orElse(() => falseCell)
    );

    this.plane = plane;
    this.wwdObject = wwdObject;
    this.position = position;
    this.correctedPosition = correctedPosition;
    this.i = i;
    this.image = image;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
    this.id = id;
  }
}