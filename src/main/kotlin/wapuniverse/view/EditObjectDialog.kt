package wapuniverse.view

import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.model.EditObjectDialog
import wapuniverse.view.util.loadFxml

private const val TITLE = "Edit Object"
private const val FXML = "/view/EditObjectDialog.fxml"

fun editObjectDialog(editObjectDialog: EditObjectDialog): Stage {
    val root = loadFxml(FXML) { EditObjectDialogController(editObjectDialog) }
    return Stage().apply {
        title = TITLE
        scene = Scene(root)
//        initModality(Modality.APPLICATION_MODAL)
        show()
    }
}
