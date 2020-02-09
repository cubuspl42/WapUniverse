export function decode(s: Uint8Array): string {
    return new TextDecoder().decode(s);
}
