import React, {useEffect} from 'react';

import './App.css';
import 'typeface-roboto';
import usePromise from "react-promise";
import {App} from "./Editor";
import {EditorUi} from "./EditorUi";
import {useCell} from "./hooks";

interface AppUiProps {
  app: App;
}

export function AppUi({app}: AppUiProps) {
  console.log("AppUi");

  const editorPromise = useCell(app.editor);
  const {value, error} = usePromise(editorPromise);
  useEffect(() => console.log("AppUi.componentDidMount"), []);

  if (value !== undefined) {
    console.log("<EditorUi ...>");
    const editorUi = <EditorUi editor={value}/>
    console.log("Post <EditorUi ...>");
    return editorUi;
  } else if (!!error) {
    return <span>Error: {error.message}</span>
  } else {
    return <span>Loading...</span>
  }
}
