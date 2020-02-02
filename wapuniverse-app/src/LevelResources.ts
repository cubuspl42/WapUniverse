import { RezIndex } from "./rezIndex";
import * as PIXI from "pixi.js";
import { Maybe } from "./Maybe";
import { Texture } from "./renderer/Renderer";
import { Vec2 } from "./Vec2";

type LoaderResource = PIXI.LoaderResource;
type LoaderDictionary = Partial<Record<string, LoaderResource>>

function loadImage(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve(img);
    img.onerror = reject;
    img.src = src;
  })
}

export interface GameImage {
  readonly pidPath: string;
  readonly img: HTMLImageElement;
  readonly size: Vec2;
  readonly offset: Vec2;
}

export class LevelResources {
  readonly imageMap: Map<string, GameImage>;

  private constructor(imageMap: Map<string, GameImage>) {
    this.imageMap = imageMap;
  }

  getGameImage(pidPath: string): Maybe<GameImage> {
    return Maybe.ofUndefined(this.imageMap.get(pidPath));
  }

  static async load(rezIndex: RezIndex, level: number): Promise<LevelResources> {
    const entries = await Promise.all(Object.entries(rezIndex.imageSets)
      .filter(([imageSetId,]) =>
        imageSetId.startsWith("GAME_") ||
        imageSetId.startsWith(`LEVEL${level}_`),
      ).flatMap(([, imageSet]) => {
        return Object.values(imageSet.sprites).map(async (rezImage) => {
          const pidPath = rezImage.path;
          const pngPath = "CLAW/" + pidPath.replace(".PID", ".png");
          const img = await loadImage(pngPath);
          const [offsetX, offsetY] = rezImage.offset;
          const size = new Vec2(img.width, img.height);
          const gameImage: GameImage = {
            pidPath,
            img,
            size,
            offset: new Vec2(offsetX, offsetY),
          };
          return [pidPath, gameImage] as [string, GameImage];
        });
      }));

    const imageMap = new Map(entries);

    return new LevelResources(imageMap);
  }
}
