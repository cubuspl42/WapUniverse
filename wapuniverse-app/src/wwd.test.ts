import {readWorld} from "./wwd";
import * as fs from "fs";
import * as path from "path";

const projectDir = path.join(__dirname, "..");

function readWorldBuffer(levelIndex: number): ArrayBuffer {
  const worldPath = path.join(projectDir, "data", "CLAW.REZ", `LEVEL${levelIndex}`, "WORLDS", "WORLD.WWD");
  return fs.readFileSync(worldPath).buffer;
}

it('reads LEVEL1.WWD', () => {
  const worldBuffer = readWorldBuffer(1);
  const wwd = readWorld(worldBuffer);
});
