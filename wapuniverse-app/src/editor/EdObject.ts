import {LazyGetter} from 'lazy-get-decorator';
import {AreaSelection} from "../AreaSelection";
import {LateStreamLoop, switcher, switcherK} from "../frp";
import {GameImage, LevelResources} from "../LevelResources";
import {Maybe, none, some} from "../Maybe";
import {Rectangle} from "../Rectangle";
import {Texture} from "../renderer/Renderer";
import {RezImage, RezIndex} from "../rezIndex";
import {Vec2} from "../Vec2";
import {DrawFlags, Object_} from "../wwd";
import {Editor, ObjectMoving} from "./Editor";
import {Plane} from "./Plane";
import {World} from "./World";
import {Cell, CellSink, lambda1, Operational, Stream, Unit} from "sodium";
import {CellLoop, lambda2} from "sodiumjs";
import {ObjectEditing} from "./ObjectEditing";

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

  readonly z: Cell<number>;

  readonly i: Cell<number>;

  readonly image: Cell<GameImage>;

  readonly boundingBox: Cell<Rectangle>;

  readonly isHovered: CellSink<boolean>;

  readonly isInSelectionArea: Cell<boolean>;

  readonly id: number;

  readonly selectLate = new LateStreamLoop<Unit>();

  readonly select = this.selectLate.stream;

  readonly onSelected = this.select.map(() => this);

  readonly pin: Cell<Unit>;

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

  // @LazyGetter()
  // get isSelected(): Cell<boolean> {
  //   return this.editor.activePlaneEditor
  //     .flatMap((ape) => ape.selectedObject).rename("EdObject.isSelected/flatMap")
  //     .map((o) => o === this);
  // }

  @LazyGetter()
  get isSelected(): Cell<boolean> {
    return this.editor.selectedObjects
      .map((so) => so.has(this));
  }

  @LazyGetter()
  get isVisible(): Cell<boolean> {
    return this.editor.visibleObjects.has(this);
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

    const buildAttributeCircuit = <T>(
      initValue: T,
      extract: (oe: ObjectEditing) => Cell<T>,
    ) => {
      const changes: Stream<T> = Cell.switchS(editor.objectEditing.map(
        (moe) => moe
          .filter((oe) => oe.object === this)
          .map((oe) => Operational.value(extract(oe)))
          .getOrElse(() => new Stream<T>()),
      ));

      return changes.hold(initValue);
    }

    const buildPositionCircuit = (initialPosition: Vec2): Cell<Vec2> => {
      const buildMoveCircuit = (om: ObjectMoving, p: Vec2): Cell<Vec2> => {
        const position = om.delta.map((d) => p.add(d));
        return switcher(
          position,
          om.onEnd.map(lambda1(
            () => buildIdleCircuit(position.sample()),
            [position],
          )),
        );
      };

      const buildIdleCircuit = (previousPosition: Vec2): Cell<Vec2> => {
        const position = buildAttributeCircuit(
          previousPosition,
          (oe) => oe.position,
        );

        return switcher(
          position,
          editor.onObjectMoving
            .filter((om) => om.objects.has(this))
            .once()
            .map(lambda1(
              (om) => buildMoveCircuit(om, position.sample()),
              [position],
            )),
        );
      };

      return buildIdleCircuit(initialPosition);
    }

    const position = buildPositionCircuit(initialPosition);

    const z = buildAttributeCircuit(wwdObject.z, (oe) => oe.z);

    const i = buildAttributeCircuit(wwdObject.i, (oe) => oe.i);

    function getImageData(imageSetId: string, i: number): Maybe<GameImage> {
      return world.getRezImage(imageSetId, i).flatMap((rezImage) => getGameImage(rezImage));
    }

    const shortImageSetId = new CellSink(initialImageSet);

    const imageSetId = shortImageSetId.map((s) => world.expandShortImageSetId(s));

    const image = imageSetId.lift(i,
      (isM, i) => isM
        .flatMap(is => getImageData(is, i))
        .getOrElse(() => getImageData("GAME_IMAGES_POWERUPS_EXTRALIFE", -1).get()));

    const correctedPosition = position.lift(image, (p, i) => p.add(i.offset));

    const boundingBox = correctedPosition.lift(image, (p: Vec2, gi: GameImage) =>
      new Rectangle(p.sub(gi.size.div(2)), gi.size));

    const falseCell = new Cell(false);

    const isInSelectionArea = areaSelection.flatMap(aM => aM.map(
      (a) => {
        return a.objectsInArea
          .map(o => o.has(this));
      })
      .getOrElse(() => falseCell)
    ).rename("isInSelectionArea");

    this.plane = plane;
    this.wwdObject = wwdObject;
    this.position = position;
    this.correctedPosition = correctedPosition;
    this.z = z;
    this.i = i;
    this.image = image;
    this.boundingBox = boundingBox;
    this.isHovered = new CellSink<boolean>(false);
    this.isInSelectionArea = isInSelectionArea;
    this.id = id;

    this.pin = Cell.liftArray<Unit>([
      position,
      z,
      i,
    ]);
  }
}
