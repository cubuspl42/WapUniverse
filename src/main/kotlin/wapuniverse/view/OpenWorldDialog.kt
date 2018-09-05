package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.stage.FileChooser
import javafx.stage.Window
import wapuniverse.model.Dialog
import java.nio.file.Path

private const val TITLE = "Open World"

fun <TDialog : Dialog> presentAsFileChooser(
        dialog: ObservableValue<TDialog?>,
        consumePath: (dialog: TDialog, path: Path) -> Unit,
        parentWindow: Window
) {
    dialog.addListener { _, oldValue, dialogVal ->
        if (dialogVal != null) {
            val fileChooser = FileChooser()
                    .apply {
                        title = TITLE
                    }

            val file = fileChooser.showOpenDialog(parentWindow)

            if (file != null) {
                consumePath(dialogVal, file.toPath())
            } else {
                dialogVal.close()
            }
        }
    }
}
