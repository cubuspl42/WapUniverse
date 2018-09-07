package wapuniverse.model

import javafx.beans.value.ObservableValue
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import wapuniverse.model.util.dialogProperty
import wapuniverse.util.optionalProperty
import java.nio.file.Path
import kotlinx.coroutines.experimental.javafx.JavaFx as UI

class MainWindow {
    val newAction: Action

    val openAction: Action

    val saveAction: Action

    val newWorldDialog: ObservableValue<NewWorldDialog?>

    val openWorldDialog: ObservableValue<OpenWorldDialog?>

    val editor = optionalProperty<Editor?>()

    private val lock = Lock()

    private val job = Job()

    private val mNewWorldDialog = dialogProperty<NewWorldDialog>()

    private val mOpenWorldDialog = dialogProperty<OpenWorldDialog>()

    private val editorFactory = EditorFactory()

    init {
        newAction = action { createNewWorld() }
        openAction = action { openWorld() }
        saveAction = action {}
        newWorldDialog = mNewWorldDialog
        openWorldDialog = mOpenWorldDialog
    }

    private fun action(block: suspend () -> Unit) = Action(lock.isUnlocked) {
        launch {
            lock.lockNow()
            block()
            lock.unlock()
        }
    }

    private fun launch(block: suspend () -> Unit): Job {
        return launch(UI, parent = job) {
            block()
        }
    }

    private suspend fun createNewWorld() {
        saveCurrentWorld()
        mNewWorldDialog.set(NewWorldDialog(this))
    }

    private suspend fun openWorld() {
        saveCurrentWorld()
        mOpenWorldDialog.set(OpenWorldDialog(this))
    }

    private suspend fun saveCurrentWorld() {
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
