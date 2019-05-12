import * as PIXI from "pixi.js";

export function autoResizingPixiApplication(parent: HTMLElement) {
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
