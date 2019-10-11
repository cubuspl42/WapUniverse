import {PixiComponent} from "@inlet/react-pixi";
import * as PIXI from 'pixi.js';

interface GraphicsRectangleProps {
  readonly x: number;
  readonly y: number;
  readonly width: number;
  readonly height: number;
  readonly strokeWidth: number;
  readonly strokeColor: number;
}

export const GraphicsRectangle = PixiComponent<GraphicsRectangleProps, PIXI.Graphics>('GraphicsRectangle', {
  create: props => new PIXI.Graphics(),
  applyProps: (instance, _, props) => {
    const {x, y, width, height, strokeWidth, strokeColor} = props;
    instance.clear();
    instance.lineStyle(strokeWidth, strokeColor)
    instance.drawRect(x, y, width, height);
    instance.endFill();
  },
});
