import {ByteString, DataStream} from "./DataStream";
import * as pako from "pako";
import {Rectangle} from "./Rectangle";

export function toArrayBuffer(buffer: Buffer) {
  return buffer.buffer.slice(buffer.byteOffset, buffer.byteOffset + buffer.byteLength);
}

export class World {
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

export class Plane {
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
  readonly imageSets: ReadonlyArray<ByteString>;
  readonly objects: ReadonlyArray<Object_>;

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
    imageSets: ReadonlyArray<ByteString>,
    objects: ReadonlyArray<Object_>
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

export class Object_ {
  readonly id: number;
  readonly name: ByteString;
  readonly logic: ByteString;
  readonly imageSet: ByteString;
  readonly animation: ByteString;
  readonly x: number;
  readonly y: number;
  readonly z: number;
  readonly i: number;
  readonly addFlags: number;
  readonly dynamicFlags: number;
  readonly drawFlags: number;
  readonly userFlags: number;
  readonly score: number;
  readonly points: number;
  readonly powerup: number;
  readonly damage: number;
  readonly smarts: number;
  readonly health: number;
  readonly moveRect: Rectangle;
  readonly hitRect: Rectangle;
  readonly attackRect: Rectangle;
  readonly clipRect: Rectangle;
  readonly userRect1: Rectangle;
  readonly userRect2: Rectangle;
  readonly userValue1: number;
  readonly userValue2: number;
  readonly userValue3: number;
  readonly userValue4: number;
  readonly userValue5: number;
  readonly userValue6: number;
  readonly userValue7: number;
  readonly userValue8: number;
  readonly xMin: number;
  readonly yMin: number;
  readonly xMax: number;
  readonly yMax: number;
  readonly speedX: number;
  readonly speedY: number;
  readonly xTweak: number;
  readonly yTweak: number;
  readonly counter: number;
  readonly speed: number;
  readonly width: number;
  readonly height: number;
  readonly direction: number;
  readonly faceDir: number;
  readonly timeDelay: number;
  readonly frameDelay: number;
  readonly objectType: number;
  readonly hitTypeFlags: number;
  readonly xMoveRes: number;
  readonly yMoveRes: number;

  constructor(
    id: number,
    name: ByteString,
    logic: ByteString,
    imageSet: ByteString,
    animation: ByteString,
    x: number,
    y: number,
    z: number,
    i: number,
    addFlags: number,
    dynamicFlags: number,
    drawFlags: number,
    userFlags: number,
    score: number,
    points: number,
    powerup: number,
    damage: number,
    smarts: number,
    health: number,
    moveRect: Rectangle,
    hitRect: Rectangle,
    attackRect: Rectangle,
    clipRect: Rectangle,
    userRect1: Rectangle,
    userRect2: Rectangle,
    userValue1: number,
    userValue2: number,
    userValue3: number,
    userValue4: number,
    userValue5: number,
    userValue6: number,
    userValue7: number,
    userValue8: number,
    xMin: number,
    yMin: number,
    xMax: number,
    yMax: number,
    speedX: number,
    speedY: number,
    xTweak: number,
    yTweak: number,
    counter: number,
    speed: number,
    width: number,
    height: number,
    direction: number,
    faceDir: number,
    timeDelay: number,
    frameDelay: number,
    objectType: number,
    hitTypeFlags: number,
    xMoveRes: number,
    yMoveRes: number,
  ) {
    this.id = id;
    this.name = name;
    this.logic = logic;
    this.imageSet = imageSet;
    this.animation = animation;
    this.x = x;
    this.y = y;
    this.z = z;
    this.i = i;
    this.addFlags = addFlags;
    this.dynamicFlags = dynamicFlags;
    this.drawFlags = drawFlags;
    this.userFlags = userFlags;
    this.score = score;
    this.points = points;
    this.powerup = powerup;
    this.damage = damage;
    this.smarts = smarts;
    this.health = health;
    this.moveRect = moveRect;
    this.hitRect = hitRect;
    this.attackRect = attackRect;
    this.clipRect = clipRect;
    this.userRect1 = userRect1;
    this.userRect2 = userRect2;
    this.userValue1 = userValue1;
    this.userValue2 = userValue2;
    this.userValue3 = userValue3;
    this.userValue4 = userValue4;
    this.userValue5 = userValue5;
    this.userValue6 = userValue6;
    this.userValue7 = userValue7;
    this.userValue8 = userValue8;
    this.xMin = xMin;
    this.yMin = yMin;
    this.xMax = xMax;
    this.yMax = yMax;
    this.speedX = speedX;
    this.speedY = speedY;
    this.xTweak = xTweak;
    this.yTweak = yTweak;
    this.counter = counter;
    this.speed = speed;
    this.width = width;
    this.height = height;
    this.direction = direction;
    this.faceDir = faceDir;
    this.timeDelay = timeDelay;
    this.frameDelay = frameDelay;
    this.objectType = objectType;
    this.hitTypeFlags = hitTypeFlags;
    this.xMoveRes = xMoveRes;
    this.yMoveRes = yMoveRes;
  }

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

export function range(end: number): Array<number> {
  return [...Array(end).keys()];
}

function concat(buffer1: ArrayBuffer, buffer2: ArrayBuffer): ArrayBuffer {
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
  return range(wwdHeader.planeCount).map(() => readPlaneHeader(stream));
}

const planeNameBufferSize = 64;

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
  const zCoord = stream.readInt32();
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
  const objects = readPlaneObjects(header, wwdBuffer);
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
      const tile = stream.readInt32();
      tiles.push(tile);
    }
  }

  return tiles;
}

function readPlaneImageSets(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<ByteString> {
  const stream = new DataStream(wwdBuffer, header.imageSetsOffset);
  return range(header.imageSetCount).map(() => stream.readByteStringNullTerminated())
}

function readPlaneObjects(header: WwdPlaneHeader, wwdBuffer: ArrayBuffer): ReadonlyArray<Object_> {
  const stream = new DataStream(wwdBuffer, header.objectsOffset);
  return range(header.objectCount).map(() => readObject(stream));
}

function readObject(stream: DataStream): Object_ {
  const id = stream.readInt32();
  const nameLen = stream.readInt32();
  const logicLen = stream.readInt32();
  const imageSetLen = stream.readInt32();
  const animationLen = stream.readInt32();
  const x = stream.readInt32();
  const y = stream.readInt32();
  const z = stream.readInt32();
  const i = stream.readInt32();
  const addFlags = stream.readInt32();
  const dynamicFlags = stream.readInt32();
  const drawFlags = stream.readInt32();
  const userFlags = stream.readInt32();
  const score = stream.readInt32();
  const points = stream.readInt32();
  const powerup = stream.readInt32();
  const damage = stream.readInt32();
  const smarts = stream.readInt32();
  const health = stream.readInt32();
  const moveRect = stream.readRectangle();
  const hitRect = stream.readRectangle();
  const attackRect = stream.readRectangle();
  const clipRect = stream.readRectangle();
  const userRect1 = stream.readRectangle();
  const userRect2 = stream.readRectangle();
  const userValue1 = stream.readInt32();
  const userValue2 = stream.readInt32();
  const userValue3 = stream.readInt32();
  const userValue4 = stream.readInt32();
  const userValue5 = stream.readInt32();
  const userValue6 = stream.readInt32();
  const userValue7 = stream.readInt32();
  const userValue8 = stream.readInt32();
  const xMin = stream.readInt32();
  const yMin = stream.readInt32();
  const xMax = stream.readInt32();
  const yMax = stream.readInt32();
  const speedX = stream.readInt32();
  const speedY = stream.readInt32();
  const xTweak = stream.readInt32();
  const yTweak = stream.readInt32();
  const counter = stream.readInt32();
  const speed = stream.readInt32();
  const width = stream.readInt32();
  const height = stream.readInt32();
  const direction = stream.readInt32();
  const faceDir = stream.readInt32();
  const timeDelay = stream.readInt32();
  const frameDelay = stream.readInt32();
  const objectType = stream.readInt32();
  const hitTypeFlags = stream.readInt32();
  const xMoveRes = stream.readInt32();
  const yMoveRes = stream.readInt32();

  const name = stream.readByteString(nameLen);
  const logic = stream.readByteString(logicLen);
  const imageSet = stream.readByteString(imageSetLen);
  const animation = stream.readByteString(animationLen);

  return new Object_(
    id,
    name,
    logic,
    imageSet,
    animation,
    x,
    y,
    z,
    i,
    addFlags,
    dynamicFlags,
    drawFlags,
    userFlags,
    score,
    points,
    powerup,
    damage,
    smarts,
    health,
    moveRect,
    hitRect,
    attackRect,
    clipRect,
    userRect1,
    userRect2,
    userValue1,
    userValue2,
    userValue3,
    userValue4,
    userValue5,
    userValue6,
    userValue7,
    userValue8,
    xMin,
    yMin,
    xMax,
    yMax,
    speedX,
    speedY,
    xTweak,
    yTweak,
    counter,
    speed,
    width,
    height,
    direction,
    faceDir,
    timeDelay,
    frameDelay,
    objectType,
    hitTypeFlags,
    xMoveRes,
    yMoveRes,
  );
}
