import * as wwd from "./wwd";
import * as fs from "fs";
import * as path from "path";
import {expect} from 'chai';

const projectDir = path.join(__dirname, "..");

function dataPath(...paths: string[]): string {
  return path.join(projectDir, "data", ...paths);
}

function toArrayBuffer(buffer: Buffer) {
  return buffer.buffer.slice(buffer.byteOffset, buffer.byteOffset + buffer.byteLength);
}

function readFileBuffer(path: string) {
  const buffer = fs.readFileSync(path);
  return toArrayBuffer(buffer);
}

function readRezWorldBuffer(levelIndex: number): ArrayBuffer {
  const worldPath = dataPath("CLAW.REZ", `LEVEL${levelIndex}`, "WORLDS", "WORLD.WWD");
  return readFileBuffer(worldPath);
}

function readDataWorldBuffer(filename: string): ArrayBuffer {
  const worldPath = dataPath(filename);
  return readFileBuffer(worldPath);
}

function encode(s: string): Uint8Array {
  return new TextEncoder().encode(s);
}

function decode(s: Uint8Array): string {
  return new TextDecoder().decode(s);
}

it('reads LEVEL1.WWD', () => {
  const worldBuffer = readRezWorldBuffer(1);
  wwd.readWorld(worldBuffer);
});

it('correctly reads TEST1.WWD', () => {
  const worldBuffer = readDataWorldBuffer("TEST1.WWD");
  const actual = wwd.readWorld(worldBuffer);

  const expected = new wwd.World(
    encode("Claw - Level 1"),
    encode("Monolith Productions Inc."),
    encode("November 16, 2019"),
    encode("\\PROJ\\CLAW\\CLAW.REZ"),
    encode("\\LEVEL1\\TILES"),
    encode("\\LEVEL1\\PALETTES\\MAIN.PAL"),
    155,
    110,
    encode("\\PROJ\\CLAW\\CLAW.EXE"),
    encode("LEVEL1\\IMAGES"),
    encode("GAME\\IMAGES"),
    encode(""),
    encode(""),
    encode("LEVEL"),
    encode("GAME"),
    encode(""),
    encode(""),
    [
      new wwd.Plane(
        12, // FIXME
        encode("Background"),
        64,
        64,
        50,
        50,
        50,
        -999,
        4,
        4,
        [
          1, 2, 3, 4,
          9, 10, 11, 12,
          17, 18, 19, 20,
          25, 26, 27, 28
        ],
        [encode("BACK")],
        [], // FIXME
      ),
      new wwd.Plane(
        1, // FIXME
        encode("Action"),
        64,
        64,
        100,
        100,
        50,
        0,
        4,
        4,
        [
          12, 308, -1, -1,
          926, 401, 402, -1,
          12, 403, 404, 305,
          12, 12, 12, 12
        ],
        [encode("ACTION")],
        [],
      ),
      new wwd.Plane(
        4, // FIXME
        encode("Front"),
        64,
        64,
        150,
        125,
        50,
        9000,
        4,
        4,
        [
          -1, -1, -1, -1,
          -1, -1, -1, -1,
          -1, -1, -1, -1,
          -1, -1, -1, -1,
        ],
        [encode("FRONT")],
        [],
      ),
    ],
  );

  expect(actual).to.be.deep.equal(expected);
});
