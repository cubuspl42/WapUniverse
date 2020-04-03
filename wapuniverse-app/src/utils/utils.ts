export function decode(s: Uint8Array): string {
    return new TextDecoder().decode(s);
}

export function range(end: number): Array<number> {
  return [...Array(end).keys()];
}

export function range2(start: number, end: number): Array<number> {
  return [...Array(end - start).keys()].map(i => i + start);
}
