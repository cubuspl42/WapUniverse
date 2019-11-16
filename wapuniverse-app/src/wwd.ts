import {ByteString, DataStream} from "./DataStream";
import * as pako from "pako";

class World {
  readonly name: ByteString;
  readonly author: ByteString;
  readonly dateCreatedString: ByteString;
  readonly rezFilePath: ByteString;
  readonly imageDir: ByteString;
  readonly palRez: ByteString;
  readonly startX: number;
  readonly startY: number;
  readonly launchApp: ByteString;
  readonly imageSet1: ByteString;
  readonly imageSet2: ByteString;
  readonly imageSet3: ByteString;
  readonly imageSet4: ByteString;
  readonly prefix1: ByteString;
  readonly prefix2: ByteString;
  readonly prefix3: ByteString;
  readonly prefix4: ByteString;

  readonly planes: ReadonlyArray<Plane>;

  constructor(
    name: ByteString,
    author: ByteString,
    dateCreatedString: ByteString,
    rezFilePath: ByteString,
    imageDir: ByteString,
    palRez: ByteString,
    startX: number,
    startY: number,
    launchApp: ByteString,
    imageSet1: ByteString,
    imageSet2: ByteString,
    imageSet3: ByteString,
    imageSet4: ByteString,
    prefix1: ByteString,
    prefix2: ByteString,
    prefix3: ByteString,
    prefix4: ByteString,
    planes: ReadonlyArray<Plane>
  ) {
    this.name = name;
    this.author = author;
    this.dateCreatedString = dateCreatedString;
    this.rezFilePath = rezFilePath;
    this.imageDir = imageDir;
    this.palRez = palRez;
    this.startX = startX;
    this.startY = startY;
    this.launchApp = launchApp;
    this.imageSet1 = imageSet1;
    this.imageSet2 = imageSet2;
    this.imageSet3 = imageSet3;
    this.imageSet4 = imageSet4;
    this.prefix1 = prefix1;
    this.prefix2 = prefix2;
    this.prefix3 = prefix3;
    this.prefix4 = prefix4;
    this.planes = planes;
  }
}

class Plane {
  readonly flags: number;
  readonly name: ByteString;
  readonly tileWidth: number; /* tile's width in pixels */
  readonly tileHeight: number; /* tile's height in pixels */
  readonly movementXPercent: number;
  readonly movementYPercent: number;
  readonly fillColor: number;
  readonly zCoord: number;
  readonly tilesWide: number;
  readonly tilesHigh: number;

  readonly tiles: ReadonlyArray<number>;
  readonly imageSets: ReadonlyArray<string>;
  readonly objects: ReadonlyArray<Object>;

  constructor(
    flags: number,
    name: ByteString,
    tileWidth: number,
    tileHeight: number,
    movementXPercent: number,
    movementYPercent: number,
    fillColor: number,
    zCoord: number,
    tilesWide: number,
    tilesHigh: number,
    tiles: ReadonlyArray<number>,
    imageSets: ReadonlyArray<string>,
    objects: ReadonlyArray<Object>
  ) {
    this.flags = flags;
    this.name = name;
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.movementXPercent = movementXPercent;
    this.movementYPercent = movementYPercent;
    this.fillColor = fillColor;
    this.zCoord = zCoord;
    this.tilesWide = tilesWide;
    this.tilesHigh = tilesHigh;
    this.tiles = tiles;
    this.imageSets = imageSets;
    this.objects = objects;
  }
}

class Object {
}

enum WwdHeaderFlags {
  USE_Z_COORDS = 1 << 0,
  COMPRESS = 1 << 1
}

interface WwdHeader {
  readonly size: number;
  readonly flags: number;
  readonly levelName: ByteString;
  readonly author: ByteString;
  readonly birth: ByteString;
  readonly rezFile: ByteString;
  readonly imageDir: ByteString;
  readonly palRez: ByteString;
  readonly startX: number;
  readonly startY: number;
  readonly planeCount: number;
  readonly mainBlockOffset: number;
  readonly tileDescriptionsOffset: number;
  readonly decompressedMainBlockSize: number;
  readonly checksum: number;
  readonly launchApp: ByteString;
  readonly imageSet1: ByteString;
  readonly imageSet2: ByteString;
  readonly imageSet3: ByteString;
  readonly imageSet4: ByteString;
  readonly prefix1: ByteString;
  readonly prefix2: ByteString;
  readonly prefix3: ByteString;
  readonly prefix4: ByteString;
}

enum WwdPlaneFlags {
  MAIN_PLANE = 1 << 0,
  NO_DRAW = 1 << 1,
  X_WRAPPING = 1 << 2,
  Y_WRAPPING = 1 << 3,
  AUTO_TILE_SIZE = 1 << 4,
}

interface WwdPlaneHeader {
  readonly size: number;
  readonly flags: number;
  readonly name: ByteString,
  readonly widthPx: number;
  readonly heightPx: number;
  readonly tileWidth: number; /* tile's width in pixels */
  readonly tileHeight: number; /* tile's height in pixels */
  readonly movementXPercent: number;
  readonly movementYPercent: number;
  readonly fillColor: number;
  readonly imageSetCount: number; // *
  readonly objectCount: number; // *
  readonly tilesOffset: number; // *
  readonly imageSetsOffset: number; // *
  readonly objectsOffset: number; // *
  readonly zCoord: number;
  readonly tilesWide: number;
  readonly tilesHigh: number;
}

const levelNameLength = 64;
const authorLength = 64;
const birthLength = 64;
const rezFileLength = 256;
const imageDirLength = 128;
const palRezLength = 128;
const launchAppLength = 128;
const imageSetLength = 128;
const prefixLength = 32;

function range(end: number): Array<number> {
  return [...Array(end).keys()];
}

function concat(buffer1: ArrayBuffer, buffer2: ArrayBuffer) {
  const tmp = new Uint8Array(buffer1.byteLength + buffer2.byteLength);
  tmp.set(new Uint8Array(buffer1), 0);
  tmp.set(new Uint8Array(buffer2), buffer1.byteLength);
  return tmp.buffer;
}

function decompressBuffer(compressedBuffer: ArrayBuffer): ArrayBuffer {
  const decompressedArray = pako.inflate(new Uint8Array(compressedBuffer));
  return decompressedArray.buffer;
}

export function readWorld(wwdBuffer: ArrayBuffer): World {
  const header = readWwdHeader(wwdBuffer);
  const mainBlock = wwdBuffer.slice(header.mainBlockOffset);

  function decompressWwdBuffer(): ArrayBuffer {
    const headerBuffer = wwdBuffer.slice(0, header.size);
    const decompressedMainBlock = decompressBuffer(mainBlock);
    return concat(headerBuffer, decompressedMainBlock);
  }

  const decompressedWwdBuffer = (header.flags & WwdHeaderFlags.COMPRESS) ?
    decompressWwdBuffer() : wwdBuffer;

  const planes = readMainBlock(header, decompressedWwdBuffer);

  return new World(
    header.levelName,
    header.author,
    header.birth,
    header.rezFile,
    header.imageDir,
    header.palRez,
    header.startX,
    header.startY,
    header.launchApp,
    header.imageSet1,
    header.imageSet2,
    header.imageSet3,
    header.imageSet4,
    header.prefix1,
    header.prefix2,
    header.prefix3,
    header.prefix4,
    planes
  )
}

export function readWwdHeader(headerBuffer: ArrayBuffer): WwdHeader {
  const stream = new DataStream(headerBuffer);

  const size = stream.readUint32(); // header size
  stream.readUint32(); // 0
  const flags = stream.readUint32();
  stream.readUint32(); // 0
  const levelName = stream.readByteString(levelNameLength);
  const author = stream.readByteString(authorLength);
  const birth = stream.readByteString(birthLength);
  const rezFile = stream.readByteString(rezFileLength);
  const imageDir = stream.readByteString(imageDirLength);
  const palRez = stream.readByteString(palRezLength);
  const startX = stream.readUint32();
  const startY = stream.readUint32();
  stream.readUint32(); // ?
  const planeCount = stream.readUint32();
  const mainBlockOffset = stream.readUint32();
  const tileDescriptionsOffset = stream.readUint32();
  const decompressedMainBlockSize = stream.readUint32();
  const checksum = stream.readUint32();
  stream.readUint32(); // ?
  const launchApp = stream.readByteString(launchAppLength);
  const imageSet1 = stream.readByteString(imageSetLength);
  const imageSet2 = stream.readByteString(imageSetLength);
  const imageSet3 = stream.readByteString(imageSetLength);
  const imageSet4 = stream.readByteString(imageSetLength);
  const prefix1 = stream.readByteString(prefixLength);
  const prefix2 = stream.readByteString(prefixLength);
  const prefix3 = stream.readByteString(prefixLength);
  const prefix4 = stream.readByteString(prefixLength);

  return {
    size: size,
    flags: flags,
    levelName: levelName,
    author: author,
    birth: birth,
    rezFile: rezFile,
    imageDir: imageDir,
    palRez: palRez,
    startX: startX,
    startY: startY,
    planeCount: planeCount,
    mainBlockOffset: mainBlockOffset,
    tileDescriptionsOffset: tileDescriptionsOffset,
    decompressedMainBlockSize: decompressedMainBlockSize,
    checksum: checksum,
    launchApp: launchApp,
    imageSet1: imageSet1,
    imageSet2: imageSet2,
    imageSet3: imageSet3,
    imageSet4: imageSet4,
    prefix1: prefix1,
    prefix2: prefix2,
    prefix3: prefix3,
    prefix4: prefix4
  }
}

function readMainBlock(header: WwdHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<Plane> {
  return readPlanes(header, wwdBuffer);
}

function readPlanes(wwdHeader: WwdHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<Plane> {
  const headers = readPlaneHeaders(wwdHeader, wwdBuffer);
  return headers.map((header) => readPlane(header, wwdBuffer));
}

function readPlaneHeaders(wwdHeader: WwdHeader, wwdBuffer: ArrayBuffer) {
  const stream = new DataStream(wwdBuffer, wwdHeader.mainBlockOffset);
  const r: Iterable<number> = range(wwdHeader.planeCount);
  return range(wwdHeader.planeCount).map(() => readPlaneHeader(stream));
}

const planeNameBufferSize = 32;

function readPlaneHeader(stream: DataStream): WwdPlaneHeader {
  const size = stream.readUint32();
  stream.readUint32(); // 0
  const flags = stream.readUint32();
  stream.readUint32(); // 0
  const name = stream.readByteString(planeNameBufferSize);
  const widthPx = stream.readUint32();
  const heightPx = stream.readUint32();
  const tileWidth = stream.readUint32();
  const tileHeight = stream.readUint32();
  const tilesWide = stream.readUint32();
  const tilesHigh = stream.readUint32();
  stream.readUint32(); // 0
  stream.readUint32(); // 0
  const movementXPercent = stream.readUint32();
  const movementYPercent = stream.readUint32();
  const fillColor = stream.readUint32();
  const imageSetCount = stream.readUint32();
  const objectCount = stream.readUint32();
  const tilesOffset = stream.readUint32();
  const imageSetsOffset = stream.readUint32();
  const objectsOffset = stream.readUint32();
  const zCoord = stream.readUint32();
  stream.readUint32(); // 0
  stream.readUint32(); // 0
  stream.readUint32(); // 0

  return {
    size: size,
    flags: flags,
    name: name,
    widthPx: widthPx,
    heightPx: heightPx,
    tileWidth: tileWidth,
    tileHeight: tileHeight,
    movementXPercent: movementXPercent,
    movementYPercent: movementYPercent,
    fillColor: fillColor,
    imageSetCount: imageSetCount,
    objectCount: objectCount,
    tilesOffset: tilesOffset,
    imageSetsOffset: imageSetsOffset,
    objectsOffset: objectsOffset,
    zCoord: zCoord,
    tilesWide: tilesWide,
    tilesHigh: tilesHigh
  }
}

function readPlane(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): Plane {
  const tiles = readPlaneTiles(header, wwdBuffer);
  const imageSets = readPlaneImageSets(header, wwdBuffer);
  const objects = readPlanObjects(header, wwdBuffer);
  const plane = new Plane(
    header.flags,
    header.name,
    header.tileWidth,
    header.tileHeight,
    header.movementXPercent,
    header.movementYPercent,
    header.fillColor,
    header.zCoord,
    header.tilesWide,
    header.tilesHigh,
    tiles,
    imageSets,
    objects
  );
  return plane;
}

function readPlaneTiles(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<number> {
  const tiles: number[] = [];
  const stream = new DataStream(wwdBuffer, header.tilesOffset);

  for (const i in range(header.tilesHigh)) {
    for (const j in range(header.tilesWide)) {
      const tile = stream.readUint32();
      tiles.push(tile);
    }
  }

  return tiles;
}

function readPlaneImageSets(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<string> {
  const stream = new DataStream(wwdBuffer, header.imageSetsOffset);
  return []; // FIXME
}

function readPlanObjects(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<Object> {
  const stream = new DataStream(wwdBuffer, header.objectsOffset);
  return []; // FIXME

}
