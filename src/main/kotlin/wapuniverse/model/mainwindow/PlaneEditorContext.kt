package wapuniverse.model.mainwindow

import javafx.beans.value.ObservableValue
import wapuniverse.model.Action
import wapuniverse.model.EditObjectDialog
import wapuniverse.model.PlaneEditor
import wapuniverse.view.extensions.map

class PlaneEditorContext(
        private val planeEditor: PlaneEditor
) {
    val editAction: Action = planeEditor.editAction

    val editObjectDialog: ObservableValue<EditObjectDialog?>

//    private val mEditObjectDialog = dialogProperty<EditObjectDialog>()

    init {
        editObjectDialog = planeEditor.editObjectContext.map { EditObjectDialog(it) }
    }
}
