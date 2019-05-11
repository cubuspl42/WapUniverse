import React from 'react';
import * as PIXI from 'pixi.js'
import './Editor.css'

class EditorMd {
}

function autoResizingPixiApplication(parent: HTMLElement) {
  const application = new PIXI.Application({
    autoResize: true,
    resolution: devicePixelRatio
  });

  parent.appendChild(application.view);

  function resizeRenderer() {
    const parent = application.view.parentNode as Element;
    application.renderer.resize(parent.clientWidth, parent.clientHeight);
  }

  window.addEventListener('resize', resizeRenderer);

  resizeRenderer();

  return application;
}


class Editor extends React.Component {
  private application: PIXI.Application | null = null;

  private divElement: HTMLDivElement | null = null;

  componentDidMount() {
    const application = autoResizingPixiApplication(this.divElement!);

    const rect = new PIXI.Graphics()
      .beginFill(0xff0000)
      .drawRect(0, 0, 100, 100);

    application.stage.addChild(rect);

    this.application = application;
  }

  componentWillUnmount() {
    this.application!.destroy(true)
  }

  render() {
    return <div className='Editor' ref={el => this.divElement = el}/>;
  }
}

export default Editor;
