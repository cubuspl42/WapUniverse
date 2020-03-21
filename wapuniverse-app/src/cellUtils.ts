import {Vec2} from "./Vec2";
import {Cell, CellSink} from "sodium";

export function elementSize(element: HTMLElement): Cell<Vec2> {
  const cell = new CellSink(new Vec2(element.offsetWidth, element.offsetHeight));

  new ResizeObserver(() => {
    const size = new Vec2(element.offsetWidth, element.offsetHeight);
    cell.send(size);
  }).observe(element);

  return cell;
}
