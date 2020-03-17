import React from 'react';

import './App.css';
import 'typeface-roboto';
import usePromise from "react-promise";
import {App} from "./editor/Editor";
import {EditorUi} from "./EditorUi";
import {useCell} from "./hooks";

interface AppUiProps {
  app: App;
}

export function AppUi({app}: AppUiProps) {
  const editorPromise = useCell(app.editor);
  const {value, error} = usePromise(editorPromise);
  const stack = error && error.stack &&
      error.stack.split("\n")
      .map((l) => <span>{l}</span>)
    || [];
  if (value !== undefined) {
    return <EditorUi editor={value}/>
  } else if (!!error) {
    return <div>
      <span>Error: {error.toString()}</span>
      <span>Stack trace:</span>
      <div>{stack}</div>
    </div>
  } else {
    return <span>Loading...</span>
  }
}
