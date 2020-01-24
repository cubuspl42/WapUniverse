import {Vec2} from "./Vec2";
import {Cell, CellSink} from "./frp/Cell";

export function elementSize(element: HTMLElement): Cell<Vec2> {
  const cell = new CellSink(new Vec2(element.offsetWidth, element.offsetHeight));

  new ResizeObserver(() => {
    cell.send(new Vec2(element.offsetWidth, element.offsetHeight))
  }).observe(element);

  return cell;
}
