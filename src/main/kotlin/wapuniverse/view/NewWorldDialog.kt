package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import wapuniverse.model.Dialog
import wapuniverse.model.NewWorldDialog
import wapuniverse.view.util.loadFxml

private const val TITLE = "New World"
private const val FXML = "/view/NewWorldDialog.fxml"

fun newWorldDialog(model: NewWorldDialog): Stage {
    val root = loadFxml(FXML) { NewWorldDialogController(model) }
    return Stage().apply {
        title = TITLE
        scene = Scene(root)
        initModality(Modality.APPLICATION_MODAL)
        show()
    }
}

fun <TDialog : Dialog> presentAsStage(
        dialog: ObservableValue<TDialog?>,
        makeStage: (dialog: TDialog) -> Stage
) {
    dialog.addListener { _, oldValue, dialogVal ->
        if (dialogVal != null) {
            val stage = makeStage(dialogVal)

            dialogVal.addCloseListener {
                stage.close()
            }

            stage.setOnCloseRequest {
                dialogVal.close()
            }

            stage.show()
        }
    }
}
