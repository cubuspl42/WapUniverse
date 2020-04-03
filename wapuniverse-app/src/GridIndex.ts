import {Rectangle} from "./Rectangle";
import {Cell} from "./sodium";
import * as frp from "frp";
import {Vec2} from "./Vec2";
import * as _ from "lodash";
import {range, range2} from "./utils/utils";
import {Maybe} from "./Maybe";
import {elementSize} from "./cellUtils";

export interface GridEntry<T> {
  readonly area: Cell<Rectangle>;
  readonly element: T;
}

interface ChunkEntry<T> {
  readonly index: Vec2;
  readonly gridEntry: GridEntry<T>;
}

// aka "grid cell"
interface GridChunk<T> {
  readonly elements: frp.Set<GridEntry<T>>;
}

const chunkSize = 1024;

function groupBy<K, V>(list: V[], f: (k: V) => K): Map<K, V[]> {
  const map = new Map<K, V[]>();
  list.forEach((item) => {
    const key = f(item);
    const collection = map.get(key);
    if (!collection) {
      map.set(key, [item]);
    } else {
      collection.push(item);
    }
  });
  return map;
}

function cartesianProduct<A, B, C>(aa: A[], ab: B[], f: (a: A, b: B) => C): C[] {
  return aa.flatMap(a => ab.map(b => f(a, b)));
}

function chunkIndex(point: Vec2): Vec2 {
  return point.divS(chunkSize).floor();
}

function rectangleChunkIndices(rect: Rectangle): Vec2[] {
  const a = chunkIndex(rect.xyMin);
  const b = chunkIndex(rect.xyMax);
  const xr = range2(a.x, b.x + 1);
  const yr = range2(a.y, b.y + 1);
  return cartesianProduct(
    xr,
    yr,
    (x, y) => new Vec2(x, y),
  );
}

function rectangleChunkIndicesSet(rect: Rectangle): Set<Vec2> {
  return new Set(rectangleChunkIndices(rect));
}

export class GridIndex<T> {
  private entries: frp.Set<GridEntry<T>>;
  // private chunks: frp.Map<Vec2, GridChunk<T>>;

  constructor(entries: frp.Set<GridEntry<T>>) {
    // const chunkEntries = entries
    //   .flatMap(
    //     e => frp.Set.flattenCJ(
    //       e.area.map(
    //         a => new Set(rectangleChunkIndices(a).map<ChunkEntry<T>>(
    //           i => ({
    //             index: i,
    //             gridEntry: e,
    //           }),
    //         )),
    //       ),
    //     ),
    //   );
    //
    // const chunks = chunkEntries
    //   .groupBy((cge) => cge.index)
    //   .mapValues<GridChunk<T>>(chunkEntries => ({
    //     elements: chunkEntries.map(ce => ce.gridEntry),
    //   }));

    this.entries = entries;
    // this.chunks = chunks;
  }

  query(queryArea: Cell<Rectangle>): frp.Set<T> {
    return this.entries
      .filterC((e) => queryArea.lift(e.area,
        (qa, a) => qa.overlaps(a),
      ))
      .map((ge) => ge.element);
  }

  // query_(queryArea: Cell<Rectangle>): frp.Set<T> {
  //   const chunkIndices: frp.Set<Vec2> = frp.Set.flattenCJ(
  //     queryArea.map(r => new Set(rectangleChunkIndices(r))),
  //   );
  //
  //   const queryChunk = (index: Vec2): Cell<Maybe<frp.Set<T>>> =>
  //     this.chunks.get(index).map((mch) => mch.map(
  //       (ch) => ch.elements
  //         .filterC((e: GridEntry<T>) => e.area.lift(queryArea,
  //           (a, qa) => a.overlaps(qa),
  //         ))
  //         .map(e => e.element),
  //     ));
  //
  //   const elements: frp.Set<T> = frp.Set.flatten(frp.Set.filterSome(
  //     chunkIndices.flatMapC(queryChunk),
  //   ));
  //
  //   return elements;
  // }
}
