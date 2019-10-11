import React from 'react';
import ReactDOM from 'react-dom';
import {AppUi} from './App';
import {App} from './Editor';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<AppUi app={new App()}/>, div);
  ReactDOM.unmountComponentAtNode(div);
});
