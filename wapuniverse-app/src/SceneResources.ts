import { Maybe } from "./Maybe";
import { Texture } from "./renderer/Renderer";
import { LevelResources } from "./LevelResources";

export class SceneResources {
  private readonly textureMap: Map<string, Texture>;

  constructor(levelResources: LevelResources) {
    const textureMapEntries = Array.from(levelResources.imageMap)
      .map(([s, gi]) => [s, Texture.fromImage(gi.img)] as [string, Texture]);
    this.textureMap = new Map(textureMapEntries);
  }

  getGameImage(pidPath: string): Maybe<Texture> {
    return Maybe.ofUndefined(this.textureMap.get(pidPath));
  }
}
