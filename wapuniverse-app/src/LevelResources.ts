import {RezIndex} from "./rezIndex";
import * as PIXI from "pixi.js";
import {Maybe} from "./Maybe";
import {Texture} from "./renderer/Renderer";

type LoaderResource = PIXI.LoaderResource;
type LoaderDictionary = Partial<Record<string, LoaderResource>>

export class LevelResources {
  private readonly _resourceDictionary: LoaderDictionary;

  private constructor(levelResources: LoaderDictionary) {
    this._resourceDictionary = levelResources;
  }

  getTexture(pidPath: string): Maybe<Texture> {
    return Maybe
      .ofUndefined(this._resourceDictionary[pidPath])
      .map((r) => new Texture(r.texture));
  }

  static load(rezIndex: RezIndex, level: number): Promise<LevelResources> {
    return new Promise(resolve => {
      const loader = new PIXI.Loader();
      Object.entries(rezIndex.imageSets)
        .filter(([imageSetId,]) =>
          imageSetId.startsWith("GAME_") ||
          imageSetId.startsWith(`LEVEL${level}_`),
        ).forEach(([, imageSet]) => {
          Object.values(imageSet.sprites).forEach((image) => {
              const pidPath = image.path;
              const pngPath = "CLAW/" + pidPath.replace(".PID", ".png");
              loader.add(pidPath, pngPath);
            }
          );
        }
      );
      loader.load((_loader, resources) => {
        resolve(new LevelResources(resources));
      });
    });
  }
}
