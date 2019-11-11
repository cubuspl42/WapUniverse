import {ByteString, DataStream} from "./DataStream";

interface WwdHeader {
  readonly flags: number,
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

const levelNameLength = 64;
const authorLength = 64;
const birthLength = 64;
const rezFileLength = 256;
const imageDirLength = 128;
const palRezLength = 128;
const launchAppLength = 128;
const imageSetLength = 128;
const prefixLength = 32;

export function readWwdHeader(stream: DataStream): WwdHeader {
  stream.readUint32(); // header size
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
