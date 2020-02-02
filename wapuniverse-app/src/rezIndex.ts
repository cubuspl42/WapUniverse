import yaml from "js-yaml";

export interface RezImage {
  offset: [number, number]; // [int, int]
  path: string; // path to PID file inside REZ
}

export interface RezImageSet {
  frames: { [frameIndex: number]: string } // -> pidFileName
  sprites: { [pidFileName: string]: RezImage }
}

export interface RezIndex {
  imageSets: { [imageSetId: string]: RezImageSet };
}

export async function fetchRezIndex() {
  const response = await fetch("rezIndex.yaml");
  const text = await response.text();
  const root = yaml.load(text) as RezIndex;
  return root
}
