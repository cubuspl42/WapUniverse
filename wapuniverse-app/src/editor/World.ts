import * as wwd from "../wwd";
import { Plane } from "./Plane";
import { decode } from "../utils/utils";
import { Maybe, None, none, some } from "../Maybe";
import { RezImage } from "../rezIndex";
import _ from "lodash";
import { Editor } from "./Editor";

interface PrefixEntry {
    readonly prefix: string;
    readonly expansion: string;
}

export class World {
    readonly editor: Editor;

    readonly levelIndex: number;

    readonly imageSets: ReadonlyArray<PrefixEntry>;

    readonly planes: ReadonlyArray<Plane>;

    constructor(
        editor: Editor,
        wwdWorld: wwd.World,
        levelIndex: number,
    ) {
        //     const levelIndexMatch = decode(wwdWorld.name).match(/\d+/);
        //     if (levelIndexMatch == null) throw Error("Level index not present in world name");

        // const action = _.maxBy(wwdWorld.planes, (p) => p.objects.length)!;

        this.editor = editor;

        this.levelIndex = levelIndex;

        this.imageSets = [
            { prefix: decode(wwdWorld.prefix1), expansion: decode(wwdWorld.imageSet1) },
            { prefix: decode(wwdWorld.prefix2), expansion: decode(wwdWorld.imageSet2) },
            { prefix: decode(wwdWorld.prefix3), expansion: decode(wwdWorld.imageSet3) },
            { prefix: decode(wwdWorld.prefix4), expansion: decode(wwdWorld.imageSet4) }
        ];

        this.planes = wwdWorld.planes.map((p) => new Plane(this, p));
    }

    expandShortImageSetId(shortImageSetId: String): Maybe<string> {
        function expandPrefix(prefixEntry: PrefixEntry): Maybe<string> {
            const sanitizedExpansion = prefixEntry.expansion.replace('\\', '_');
            return Maybe.test(shortImageSetId.startsWith(prefixEntry.prefix),
                () => shortImageSetId.replace(prefixEntry.prefix, sanitizedExpansion));
        }

        const expandedPrefixes = this.imageSets.map(expandPrefix);

        return Maybe.findSome(expandedPrefixes);
    }

    getRezImage(imageSetId: string, i: number): Maybe<RezImage> {
        const rezImageSet = this.editor.rezIndex.imageSets[imageSetId];
        if (!rezImageSet) return none();
        const pidFileName = rezImageSet.frames[i];
        if (!pidFileName) return none();
        return some(rezImageSet.sprites[pidFileName]);
    }
}
