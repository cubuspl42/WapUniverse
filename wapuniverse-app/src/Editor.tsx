import React from 'react';
import * as PIXI from 'pixi.js';
import yaml from 'js-yaml';
import {autoResizingPixiApplication} from "./pixiUtils";

import './Editor.css';

class EditorMd {
}

interface Image {
  offset: [number]; // [int, int]
  path: string; // path to PID file inside REZ
}

interface ImageSet {
  frames: { [frameIndex: number]: string } // -> pidFileName
  sprites: { [pidFileName: string]: Image }
}

interface RezIndex {
  imageSets: { [imageSetId: string]: ImageSet };
}

async function fetchRezIndex() {
  const response = await fetch("rezIndex.yaml");
  const text = await response.text();
  const root = yaml.load(text) as RezIndex;
  return root
}

async function loadResources(rezIndex: RezIndex): Promise<PIXI.loaders.ResourceDictionary> {
  return new Promise(function (resolve, reject) {
    const loader = new PIXI.loaders.Loader();
    Object.entries(rezIndex.imageSets)
      .filter(([imageSetId,]) => imageSetId.startsWith("LEVEL1_"))
      .forEach(([, imageSet]) => {
          Object.values(imageSet.sprites).forEach((image) => {
              const pngPath = "CLAW/" + image.path.replace(".PID", ".png");
              loader.add(pngPath)
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
    const officerTexture = resources["CLAW/LEVEL1/IMAGES/OFFICER/FRAME001.png"];
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
