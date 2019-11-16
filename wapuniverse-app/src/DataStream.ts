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

  readUint32(): number {
    const value = this._dataView.getUint32(this._offset, this._littleEndian);
    this._offset += 4;
    return value;
  }

  readByteString(length: number): ByteString {
    const byteString = new Uint8Array(this._arrayBuffer, this._offset, length);
    this._offset += length;
    return byteString;
  }
}
