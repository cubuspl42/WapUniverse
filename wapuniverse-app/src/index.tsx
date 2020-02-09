import React from 'react';
import ReactDOM from 'react-dom';

import './index.css';
import { AppUi } from "./App";
import { App } from "./editor/Editor";
import { Transaction } from 'sodiumjs';

Transaction.run(() => {
    ReactDOM.render(<AppUi app={new App()} />, document.getElementById('root'));
});
