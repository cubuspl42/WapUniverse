import React from 'react';
import ReactDOM from 'react-dom';

import './index.css';
import {AppUi} from "./App";
import {App} from "./editor/Editor";
import {Transaction} from 'sodiumjs';
import {StylesProvider} from '@material-ui/core/styles';

Transaction.run(() => {
  ReactDOM.render(
    <StylesProvider injectFirst>
      <AppUi app={new App()}/>
    </StylesProvider>,
    document.getElementById('root'),
  );
});
