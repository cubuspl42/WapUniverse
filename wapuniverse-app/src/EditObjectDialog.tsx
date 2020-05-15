import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {Editor} from "./editor/Editor";
import {useCell} from "./hooks";
import {TextField} from "@material-ui/core";
import {ObjectEditing} from "./editor/ObjectEditing";
import {Cell} from "./sodium";
import {makeStyles} from "@material-ui/core/styles";

interface MaybeEditObjectDialogProps {
  editor: Editor;
}

export function MaybeEditObjectDialog({editor}: MaybeEditObjectDialogProps) {
  const objectEditing = useCell(editor.objectEditing);
  return objectEditing.fold(
    () => <div/>,
    (oe) => <EditObjectDialog objectEditing={oe}/>,
  );
}

interface EditObjectDialogProps {
  objectEditing: ObjectEditing;
}

const useStyles = makeStyles((theme) => ({
  root: {
    '& .MuiTextField-root': {
      margin: theme.spacing(1),
    },
  },
}));

export function EditObjectDialog({objectEditing}: EditObjectDialogProps) {
  const classes = useStyles();

  const oe = objectEditing;
  return <Dialog open={true} onClose={() => objectEditing.doEnd()}>
    <DialogTitle id="alert-dialog-title">Edit object</DialogTitle>
    <DialogContent className={classes.root}>
      <DialogContentText id="alert-dialog-description">
        ID: {objectEditing.object.id}
      </DialogContentText>
      <form noValidate autoComplete="off">
        <div>
          <NumericTextField label={"X"} cell={oe.x} set={(x) => oe.setX(x)}/>
          <NumericTextField label={"Y"} cell={oe.y} set={(y) => oe.setY(y)}/>
        </div>
        <div>
          <NumericTextField label={"Z"} cell={oe.z} set={(z) => oe.z.send(z)}/>
          <NumericTextField label={"I"} cell={oe.i} set={(i) => oe.i.send(i)}/>
        </div>
      </form>
    </DialogContent>
    <DialogActions>
      <Button color="primary" autoFocus>
        Agree
      </Button>
    </DialogActions>
  </Dialog>;
}

function StringTextField(props: {
  label: string,
  cell: Cell<string>,
  set: (value: string) => void,
}) {
  const {cell, set} = props;
  const value = useCell(cell);
  return <TextField
    id="standard-basic" label={props.label} defaultValue={value}
    onChange={
      (e) =>
        set(e.target.value)
    }
  />;
}

function NumericTextField(props: {
  label: string,
  cell: Cell<number>,
  set: (value: number) => void,
}) {
  const {cell, set} = props;
  const value = useCell(cell);
  return <TextField
    id="standard-basic" label={props.label} type="number" defaultValue={value}
    onChange={
      (e) =>
        set(parseInt(e.target.value, 10))
    }
  />;
}


