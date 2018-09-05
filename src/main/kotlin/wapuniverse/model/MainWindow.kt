package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.model.util.dialogProperty
import wapuniverse.util.optionalProperty

class MainWindow {
    val newAction = Action { createNewWorld() }

    val openAction = Action {}

    val saveAction = Action {}

    val newWorldDialog: ObservableValue<NewWorldDialog?>

    val editor = optionalProperty<Editor?>()

    private val mNewWorldDialog = dialogProperty<NewWorldDialog>()

    init {
        newWorldDialog = mNewWorldDialog


    }

    private fun createNewWorld() {
        check(newWorldDialog.value == null)
        saveCurrentWorld()
        mNewWorldDialog.set(NewWorldDialog(this))
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
        editor.set(Editor(baseLevel))
    }
}
