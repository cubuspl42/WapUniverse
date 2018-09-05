package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.model.util.dialogProperty
import wapuniverse.util.optionalProperty
import java.nio.file.Path

class MainWindow {
    val newAction = Action { createNewWorld() }

    val openAction = Action { openWorld() }

    val saveAction = Action {}

    val newWorldDialog: ObservableValue<NewWorldDialog?>

    val openWorldDialog: ObservableValue<OpenWorldDialog?>

    val editor = optionalProperty<Editor?>()

    private val mNewWorldDialog = dialogProperty<NewWorldDialog>()

    private val mOpenWorldDialog = dialogProperty<OpenWorldDialog>()

    private val editorFactory = EditorFactory()

    init {
        newWorldDialog = mNewWorldDialog
        openWorldDialog = mOpenWorldDialog
    }

    private fun createNewWorld() {
        check(newWorldDialog.value == null)
        saveCurrentWorld()
        mNewWorldDialog.set(NewWorldDialog(this))
    }

    private fun openWorld() {
        check(openWorldDialog.value == null)
        saveCurrentWorld()
        mOpenWorldDialog.set(OpenWorldDialog(this))
    }

    private fun saveCurrentWorld() {
        editor.value?.let {
            if (!it.isSaved()) {
                it.save()
            }
            editor.clear()
        }
    }

    internal fun createNewWorld(baseLevel: BaseLevel) {
        editor.set(editorFactory.createEditor(baseLevel))
    }

    internal fun openWorld(worldPath: Path) {
        editor.set(editorFactory.createEditor(worldPath))
    }
}
