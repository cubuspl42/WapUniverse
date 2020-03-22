import { Cell } from "sodium";
import { EdObject } from "./EdObject";
import { Plane } from "./Plane";
import { Stream } from "sodium";
import { stopwatch } from "./Editor";

function orElseMany<T>(streams: Stream<T>[]) {
  return orElseMany_(streams, 0, streams.length);
}

function orElseMany_<T>(streams: Stream<T>[], start: number, end: number): Stream<T> {
  const len = end - start;
  if (len == 0) return new Stream<T>();
  else if (len == 1) return streams[start];
  else {
    const mid = ~~((start + end) / 2);
    const left = orElseMany_(streams, start, mid);
    const right = orElseMany_(streams, mid, end);
    return left.orElse(right);
  }
}

export class ActivePlaneEditor {
  readonly plane: Plane;

  readonly selectedObject: Cell<EdObject>;

  constructor(plane: Plane) {
    this.plane = plane;
    const selectObject = stopwatch("orElseMany", () => Stream.firstOf(plane.objects.map((o) => o.onSelected)));
    this.selectedObject = new Cell(plane.objects[0], selectObject);
  }
}
