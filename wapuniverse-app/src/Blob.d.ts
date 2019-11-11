export declare global {
  export interface Blob {
    stream(): ReadableStream

    arrayBuffer(): ArrayBuffer
  }
}
