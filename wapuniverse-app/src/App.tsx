import React from 'react';

import './App.css';
import 'typeface-roboto';
import usePromise from "react-promise";
import {App} from "./Editor";
import {EditorUi, useCell} from "./EditorUi";

interface AppUiProps {
  app: App;
}

export function AppUi({app}: AppUiProps) {
  const editorPromise = useCell(app.editor);
  const {value, error} = usePromise(editorPromise);
  if (value !== undefined) {
    return <EditorUi editor={value}/>
  } else if (!!error) {
    return <span>Error: {error.message}</span>
  } else {
    return <span>Loading...</span>
  }
}
