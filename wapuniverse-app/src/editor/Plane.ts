import * as wwd from "../wwd";
import { Matrix } from "../Matrix";
import { EdObject } from "./EdObject";
import _ from "lodash";
import { Editor } from "./Editor";
import { Vec2 } from "../Vec2";
import { decode } from "../utils/utils";
import { World } from "./World";
import { Maybe, some } from "../Maybe";
import { RezImage } from "../rezIndex";


export class Plane {
    readonly world: World;

    readonly tilesImageSet: string;

    readonly tiles: Matrix<number>;

    readonly objects: ReadonlyArray<EdObject>;

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
            wwdPlane.objects.map((o) => {
                const x = wwd.copyObject(o, { height: 2 });
                return new EdObject(
                    this,
                    this.editor.rezIndex,
                    this.editor.levelResources,
                    this.editor.areaSelection,
                    o,
                    new Vec2(o.x, o.y),
                    decode(o.imageSet),
                    o.id,
                );
            });

        console.log(`Object count: ${this.objects.length}`);

    }

    getTileRezImage(tileId: number): Maybe<RezImage> {
        const world = this.world;
        return some(tileId)
            .filter((t) => t >= 0)
            .flatMap((t) => world.getRezImage(`LEVEL${world.levelIndex}_TILES_${this.tilesImageSet}`, tileId));
    }
}
