import * as wwd from "../wwd";
import {Matrix} from "../Matrix";
import {EdObject} from "./EdObject";
import _ from "lodash";
import {Editor} from "./Editor";
import {Vec2} from "../Vec2";
import {decode} from "../utils/utils";
import {World} from "./World";
import {Maybe, some} from "../Maybe";
import {RezImage} from "../rezIndex";
import * as frp from "frp";
import {SetChange} from "../frp/Set";
import {Stream} from "../sodium";
import {Cell, Unit} from "sodiumjs";

export class Plane {
  readonly world: World;

  readonly tilesImageSet: string;

  readonly tiles: Matrix<number>;

  readonly objects: frp.Set<EdObject>;

  readonly pin: Cell<Unit>;

  get editor(): Editor {
    return this.world.editor;
  }

  constructor(
    world: World,
    wwdPlane: wwd.Plane,
  ) {
    this.world = world;

    this.tilesImageSet = decode(wwdPlane.imageSets[0]);

    this.tiles = new Matrix(wwdPlane.tilesWide, wwdPlane.tilesHigh, wwdPlane.tiles);

    this.objects =
      frp.Set.hold(
        new Set(wwdPlane.objects.map((o) =>
          new EdObject(
            this,
            this.editor.rezIndex,
            this.editor.levelResources,
            this.editor.areaSelection,
            o,
            new Vec2(o.x, o.y),
            decode(o.imageSet),
            o.id,
          ))),
        this.editor.deleteObjects.map(
          (objects) => {
            console.log(`deleteObjects.map`, {objects, editor: this.editor});
            return SetChange.remove(objects);
          },
        ),
      );

    console.log(`Object count: ${this.objects.sample().size}`);

    this.pin = this.objects.pin((o) => o.pin);
  }

  getTileRezImage(tileId: number): Maybe<RezImage> {
    const world = this.world;
    return some(tileId)
      .filter((t) => t >= 0)
      .flatMap((t) => world.getRezImage(`LEVEL${world.levelIndex}_TILES_${this.tilesImageSet}`, tileId));
  }
}
