import {Rectangle} from "./Rectangle";

export type ByteString = Uint8Array;

export class DataStream {
  private readonly _arrayBuffer: ArrayBuffer;

  private readonly _dataView: DataView;

  private _offset = 0;

  private _littleEndian = true;

  constructor(arrayBuffer: ArrayBuffer, initialOffset: number = 0) {
    this._arrayBuffer = arrayBuffer;
    this._dataView = new DataView(arrayBuffer);
    this._offset = initialOffset;
  }

  readInt32() {
    const value = this._dataView.getInt32(this._offset, this._littleEndian);
    this._offset += 4;
    return value;
  }

  readUint32(): number {
    const value = this._dataView.getUint32(this._offset, this._littleEndian);
    this._offset += 4;
    return value;
  }

  readByteString(length: number): ByteString {
    const fullByteString = new Uint8Array(this._arrayBuffer, this._offset, length);
    const firstZeroIndex = fullByteString.indexOf(0);
    const byteString = firstZeroIndex >= 0 ?
      fullByteString.slice(0, firstZeroIndex) :
      fullByteString;
    this._offset += length;
    return byteString;
  }

  readByteStringNullTerminated(): ByteString {
    const bytes = [];
    while (true) {
      const byte = this._dataView.getUint8(this._offset);
      ++this._offset;
      if (byte === 0) break;
      bytes.push(byte);
    }
    return new Uint8Array(bytes);
  }

  readRectangle(): Rectangle {
    return Rectangle.fromBounds(
      this.readInt32(),
      this.readInt32(),
      this.readInt32(),
      this.readInt32(),
    );
  }
}
