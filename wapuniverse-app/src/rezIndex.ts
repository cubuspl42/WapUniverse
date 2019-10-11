import yaml from "js-yaml";

export interface Image {
  offset: [number, number]; // [int, int]
  path: string; // path to PID file inside REZ
}

export interface ImageSet {
  frames: { [frameIndex: number]: string } // -> pidFileName
  sprites: { [pidFileName: string]: Image }
}

export interface RezIndex {
  imageSets: { [imageSetId: string]: ImageSet };

}

export async function fetchRezIndex() {
  const response = await fetch("rezIndex.yaml");
  const text = await response.text();
  const root = yaml.load(text) as RezIndex;
  return root
}
