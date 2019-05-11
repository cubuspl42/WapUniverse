import React, {ReactNode} from 'react';
import './App.css';
import 'typeface-roboto';
import Editor from "./Editor";

class App extends React.Component {

  componentDidMount() {

  }

  render() {
    return (
      <div className='App'>
        <Editor/>
      </div>
    );
  }
}

export default App;
