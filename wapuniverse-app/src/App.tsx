import React from 'react';

import './App.css';
import 'typeface-roboto';
import usePromise from "react-promise";
import {App} from "./Editor";
import {EditorUi} from "./EditorUi";

interface AppUiProps {
  app: App;
}

export function AppUi({app}: AppUiProps) {
  const {value,} = usePromise(app.editor);
  if (value !== undefined) {
    return <EditorUi editor={value}/>
  } else {
    return <span>Loading...</span>
  }
}
