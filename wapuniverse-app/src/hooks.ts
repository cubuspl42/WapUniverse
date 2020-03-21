import {useEffect, useMemo, useState} from "react";
import {Cell} from "sodium";

type CellProvider<T> = () => Cell<T>;

export function useCell<T>(cell: Cell<T> | CellProvider<T>): T {
  const cell_ = useMemo<Cell<T>>(
    cell instanceof Cell ? () => {
      return cell;
    } : cell, []);

  const [value, setValue] = useState(cell_.sample());

  useEffect(() => {
    return cell_.listen(setValue);
  }, []);

  return value;
}
