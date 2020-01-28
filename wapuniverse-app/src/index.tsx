import React from 'react';
import ReactDOM from 'react-dom';

import './index.css';
import {AppUi} from "./App";
import {App} from "./Editor";

ReactDOM.render(<AppUi app={new App()}/>, document.getElementById('root'));
console.log("Post render");
