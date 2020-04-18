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

export function EditObjectDialog({objectEditing}: EditObjectDialogProps) {
  const position = useCell(objectEditing.position);
  return <Dialog open={true} onClose={() => objectEditing.doEnd()}>
    <DialogTitle id="alert-dialog-title">Edit object</DialogTitle>
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        ID: {objectEditing.object.id}
      </DialogContentText>
      <form noValidate autoComplete="off">
        <div>
          <TextField
            id="standard-basic" label="X" type="number" defaultValue={position.x}
            onChange={
              (e) =>
                objectEditing.setX(parseInt(e.target.value, 10))
            }
          />
          <TextField
            id="standard-basic" label="Y" type="number" defaultValue={position.y}
            onChange={
              (e) =>
                objectEditing.setY(parseInt(e.target.value, 10))
            }
          />
        </div>
        <div>
          <TextField id="standard-basic" label="Z"/>
          <TextField id="standard-basic" label="I"/>
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

