import React from 'react';
import * as PIXI from 'pixi.js';
import {autoResizingPixiApplication} from "./pixiUtils";

import './Editor.css';
import {fetchRezIndex, RezIndex} from "./rezIndex";

class EditorMd {
}

async function loadResources(rezIndex: RezIndex): Promise<PIXI.loaders.ResourceDictionary> {
  return new Promise(resolve => {
    const loader = new PIXI.loaders.Loader();
    Object.entries(rezIndex.imageSets)
      .filter(([imageSetId,]) => imageSetId.startsWith("LEVEL1_"))
      .forEach(([, imageSet]) => {
          Object.values(imageSet.sprites).forEach((image) => {
              const pidPath = image.path;
              const pngPath = "CLAW/" + pidPath.replace(".PID", ".png");
              loader.add(pidPath, pngPath);
            }
          );
        }
      );
    loader.load((_loader, resources: PIXI.loaders.ResourceDictionary) => {
      resolve(resources);
    });
  });
}

class Editor extends React.Component {
  private application: PIXI.Application | null = null;

  private divElement: HTMLDivElement | null = null;

  componentDidMount() {
    this.application = autoResizingPixiApplication(this.divElement!);

    this.init();
  }

  async init() {
    const rezIndex = await fetchRezIndex();
    const resources = await loadResources(rezIndex);
    const officerTexture = resources["LEVEL1/IMAGES/OFFICER/FRAME001.PID"];
    this.application!.stage.addChild(new PIXI.Sprite(officerTexture.texture));
  }


  componentWillUnmount() {
    this.application!.destroy(true)
  }

  render() {
    return <div className='Editor' ref={el => this.divElement = el}/>;
  }
}

export default Editor;
