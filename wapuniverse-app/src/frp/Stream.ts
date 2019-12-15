export class Stream<T> {
  static never<T>() {
    return new Stream<T>();
  }
}
