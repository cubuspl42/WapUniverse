import * as fs from 'fs';
import * as wwd from '../../wwd';
import program from 'commander';
import glob from 'glob';
import * as _ from 'lodash';

interface PlaneStats {
  name: string;
  tilesSize: [number, number];
  objectsCount: number;
}

interface WorldStats {
  world: {
    filePath: string;
    name: string;
    planes: PlaneStats[];
  }
}

function decode(s: Uint8Array): string {
  return new TextDecoder().decode(s);
}

program
  .version('0.0.1')
  .arguments(`<dir>`)
  .action((dirPath: string) => {
    glob(`${dirPath}/*.wwd`, (err, files: string[]) => {
      if (err) {
        console.log(`Error: ${err}`);
        return;
      }

      const stats = files.map((filePath): WorldStats => {
        const buffer = fs.readFileSync(filePath);
        const wwdBuffer = wwd.toArrayBuffer(buffer);
        const world = wwd.readWorld(wwdBuffer);

        return {
          world: {
            filePath: filePath,
            name: decode(world.name),
            planes: world.planes.map((p) => ({
              name: decode(p.name),
              tilesSize: [p.tilesWide, p.tilesHigh],
              objectsCount: p.objects.length
            }))
          },
        };
      });


      const maxStats = _.sortBy(stats, (stats): number => {
        return -_.max(stats.world.planes.map((p) => _.max(p.tilesSize)!))!;
      });

      const statsJson = JSON.stringify(maxStats, null, 2);
      fs.writeFileSync("stats.json", statsJson);

      const maxStatsJson = JSON.stringify(maxStats, null, 2);
      console.log(maxStatsJson);
    });
  })
  .parse(process.argv);
