import React, {useState} from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import {Editor} from "./editor/Editor";
import {useCell} from "./hooks";
import {Checkbox, FormControl, FormControlLabel, FormGroup, Paper, Tab, Tabs, TextField} from "@material-ui/core";
import {ObjectEditing} from "./editor/ObjectEditing";
import {Cell} from "./sodium";
import {makeStyles} from "@material-ui/core/styles";
import {range2} from "./utils/utils";
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormLabel from '@material-ui/core/FormLabel';

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

  const [tabIndex, setTabIndex] = useState(0);

  const buildTabPanel = () => {
    switch (tabIndex) {
      case 0:
        return <AttributesPanel objectEditing={oe}/>;
      case 1:
        return <FlagsPanel objectEditing={oe}/>;
      case 2:
        return <RectsPanel objectEditing={oe}/>;
    }
  }

  return <Dialog maxWidth={false} open={true} onClose={() => objectEditing.doEnd()}>
    <div style={{width: 900, height: 768}}>
      <Paper square>
        <DialogTitle>Edit object</DialogTitle>
        <Tabs
          value={tabIndex}
          indicatorColor="primary"
          textColor="primary"
          onChange={(_, ti) => setTabIndex(ti)}
        >
          <Tab label="Attributes"/>
          <Tab label="Flags & hits"/>
          <Tab label="Rects"/>
        </Tabs>
      </Paper>
      <DialogContent style={{paddingTop: 20}} className={classes.root}>
        {buildTabPanel()}
      </DialogContent>
      <DialogActions>
        <Button color="primary" autoFocus>
          Agree
        </Button>
      </DialogActions>
    </div>
  </Dialog>;
}

function AttributesPanel(props: { objectEditing: ObjectEditing }) {
  const oe = props.objectEditing;
  return <form style={{
    display: "flex",
    flexDirection: "row",
  }} noValidate autoComplete="off">
    <div style={{
      display: "flex",
      flexDirection: "column",
    }}>
      <NumericTextField label={"ID"} cell={new Cell(0)} set={(a) => {
      }}/>
      <StringTextField label={"Name"} cell={oe.name} set={(v) => oe.name.send(v)}/>
      <StringTextField label={"Logic"} cell={oe.logic} set={(v) => oe.logic.send(v)}/>
      <StringTextField label={"Image Set"} cell={oe.imageSet} set={(v) => oe.imageSet.send(v)}/>
      <StringTextField label={"Animation"} cell={oe.animation} set={(v) => oe.animation.send(v)}/>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"X"} cell={oe.x} set={(x) => oe.setX(x)}/>
        <NumericTextField label={"Y"} cell={oe.y} set={(y) => oe.setY(y)}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Z"} cell={oe.z} set={(z) => oe.z.send(z)}/>
        <NumericTextField label={"I"} cell={oe.i} set={(i) => oe.i.send(i)}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Speed X"} cell={new Cell(0)} set={(x) => null}/>
        <NumericTextField label={"Speed Y"} cell={new Cell(0)} set={(y) => null}/>
      </div>
    </div>
    <div style={{
      display: "flex",
      flexDirection: "column",
    }}>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Score"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Points"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Smarts"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Powerup"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Damage"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Health"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Facedir"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Direction"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Speed"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Counter"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Width"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Height"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Time delay"} cell={new Cell(0)} set={(a) => {
        }}/>
        <NumericTextField label={"Frame delay"} cell={new Cell(0)} set={(b) => {
        }}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Tweak X"} cell={new Cell(0)} set={(x) => null}/>
        <NumericTextField label={"Tweak Y"} cell={new Cell(0)} set={(y) => null}/>
      </div>
      <div style={{display: 'flex'}}>
        <NumericTextField label={"Move res. X"} cell={new Cell(0)} set={(x) => null}/>
        <NumericTextField label={"Move res. Y"} cell={new Cell(0)} set={(y) => null}/>
      </div>
    </div>
    <div style={{
      display: "flex",
      flexDirection: "column",
    }}>
      {range2(1, 8 + 1).map((i) =>
        <NumericTextField label={`User ${i}`} cell={new Cell(0)} set={(x) => null}/>
      )}
    </div>
  </form>;
}

function FlagsPanel(props: { objectEditing: ObjectEditing }) {
  const oe = props.objectEditing;
  return <div style={{
    display: "flex",
  }}>
    <FormControl style={{flex: 1}} component="fieldset">
      <FormLabel component="legend">Add. flags</FormLabel>
      <FormGroup>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Difficult"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Eye candy"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="High detail"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Multiplayer"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Extra memory"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Fast CPU"/>
      </FormGroup>
    </FormControl>
    <div style={{flex: 1, display: "flex", flexDirection: "column"}}>
      <FormControl style={{marginBottom: 10}} component="fieldset">
        <FormLabel component="legend">Draw flags</FormLabel>
        <FormGroup>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="No draw"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Mirror"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Invert"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Flash"/>
        </FormGroup>
      </FormControl>
      <FormControl style={{flex: 1}} component="fieldset">
        <FormLabel component="legend">Dynamic flags</FormLabel>
        <FormGroup>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="No hit"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Always active"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Safe"/>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="Auto hit damage"/>
        </FormGroup>
      </FormControl>
    </div>
    <FormControl style={{flex: 1}} component="fieldset">
      <FormLabel component="legend">User flags</FormLabel>
      <FormGroup>
        {range2(1, 12 + 1).map((i) =>
          <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label={`User flag ${i}`}/>
        )}
      </FormGroup>
    </FormControl>
    <FormControl style={{flex: 1}} component="fieldset">
      <FormLabel component="legend">Type</FormLabel>
      <RadioGroup value={"GENERIC"} onChange={() => null}>
        <FormControlLabel control={<Radio/>} value="GENERIC" label="GENERIC"/>
        <FormControlLabel control={<Radio/>} value="PLAYER" label="PLAYER"/>
        <FormControlLabel control={<Radio/>} value="ENEMY" label="ENEMY"/>
        <FormControlLabel control={<Radio/>} value="POWERUP" label="POWERUP"/>
        <FormControlLabel control={<Radio/>} value="SHOT" label="SHOT"/>
        <FormControlLabel control={<Radio/>} value="PSHOT" label="PSHOT"/>
        <FormControlLabel control={<Radio/>} value="ESHOT" label="ESHOT"/>
        <FormControlLabel control={<Radio/>} value="SPECIAL" label="SPECIAL"/>
        <FormControlLabel control={<Radio/>} value="USER1" label="USER1"/>
        <FormControlLabel control={<Radio/>} value="USER2" label="USER2"/>
        <FormControlLabel control={<Radio/>} value="USER3" label="USER3"/>
        <FormControlLabel control={<Radio/>} value="USER4" label="USER4"/>
      </RadioGroup>
    </FormControl>
    <FormControl style={{flex: 1}} component="fieldset">
      <FormLabel component="legend">Hits</FormLabel>
      <FormGroup>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="GENERIC"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="PLAYER"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="ENEMY"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="POWERUP"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="SHOT"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="PSHOT"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="ESHOT"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="SPECIAL"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="USER1"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="USER2"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="USER3"/>
        <FormControlLabel control={<Checkbox checked={false} onChange={() => null}/>} label="USER4"/>
      </FormGroup>
    </FormControl>
  </div>
}

function RectsPanel(props: { objectEditing: ObjectEditing }) {
  const oe = props.objectEditing;
  return <div style={{display: "flex"}}>
    <div style={{
      display: "flex",
      flexDirection: "column",
      marginRight: 10,
    }}>
      <RectControl label="Min-max rect"/>
      <RectControl label="Move rect"/>
      <RectControl label="Hit rect"/>
      <RectControl label="Attack rect"/>
    </div>
    <div style={{
      display: "flex",
      flexDirection: "column",
    }}>
      <RectControl label="Clip rect"/>
      <RectControl label="User rect 1"/>
      <RectControl label="User rect 2"/>
    </div>
  </div>
}

function RectControl(props: {
  label: string,
}) {
  return <FormControl style={{marginBottom: 10}}>
    <FormLabel style={{marginLeft: 8, marginBottom: 8}} component="legend">{props.label}</FormLabel>
    <div style={{display: "flex"}}>
      <NumericTextField label={"X min"} cell={new Cell(0)} set={(y) => null}/>
      <NumericTextField label={"Y min"} cell={new Cell(0)} set={(y) => null}/>
    </div>
    <div style={{display: "flex"}}>
      <NumericTextField label={"X max"} cell={new Cell(0)} set={(y) => null}/>
      <NumericTextField label={"Y max"} cell={new Cell(0)} set={(y) => null}/>
    </div>
  </FormControl>
}

function StringTextField(props: {
  label: string,
  cell: Cell<string>,
  set: (value: string) => void,
}) {
  const {cell, set} = props;
  const value = useCell(cell);
  return <TextField
    variant="outlined"
    size="small"
    inputProps={{spellCheck: false}}
    label={props.label} defaultValue={value}
    onChange={(e) => set(e.target.value)}
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
    variant="outlined"
    size="small"
    label={props.label} type="number" defaultValue={value}
    onChange={(e) => set(parseInt(e.target.value, 10))}
  />;
}


