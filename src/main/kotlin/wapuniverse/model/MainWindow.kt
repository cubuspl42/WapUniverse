package wapuniverse.model

import io.github.jwap32.v1.loadWwd
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import wapuniverse.model.util.dialogProperty
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.RezIndex
import wapuniverse.rez.addImageSizes
import wapuniverse.rez.loadYamlRezIndex
import wapuniverse.util.optionalProperty
import wapuniverse.view.extensions.flatMap
import wapuniverse.view.extensions.map
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.experimental.javafx.JavaFx as UI

private const val rezIndexPath = "rezIndex.yaml"
private const val rezImageLoaderPrefix = "CLAW"

class MainWindow {
    val newAction: Action

    val openAction: Action

    val saveAction: Action

    val editAction: Action

    val tool: ObservableValue<Property<Tool>?>

    val newWorldDialog: ObservableValue<NewWorldDialog?>

    val openWorldDialog: ObservableValue<OpenWorldDialog?>

    val editObjectDialog: ObservableValue<EditObjectDialog?>

    val editor = optionalProperty<Editor?>()

    val rezIndex: RezIndex

    val rezImageLoader = ClassLoaderRezImageLoader(rezImageLoaderPrefix) // FIXME: Move

    private val lock = Lock()

    private val job = Job()

    private val mNewWorldDialog = dialogProperty<NewWorldDialog>()

    private val mOpenWorldDialog = dialogProperty<OpenWorldDialog>()

    init {
        val selectToolContext = editor.flatMap { it.planeEditor }.flatMap { it.selectToolContext }

//        newAction = action { createNewWorld() }
        newAction = Action(SimpleBooleanProperty(false)) {} // FIXME
        openAction = action { openWorld() }
        saveAction = action {}
        editAction = Action(selectToolContext.map { it.editAction })

        tool = editor.map { it.tool }

        newWorldDialog = mNewWorldDialog
        openWorldDialog = mOpenWorldDialog
        editObjectDialog = selectToolContext.flatMap { it.editObjectContext }.map(::EditObjectDialog)

        val classLoader = Thread.currentThread().contextClassLoader
        val yamlRezIndex = loadYamlRezIndex(classLoader.getResourceAsStream(rezIndexPath))
        rezIndex = addImageSizes(yamlRezIndex, rezImageLoader)
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
        editor.set(createEditor(baseLevel))
    }

    internal fun openWorld(worldPath: Path) {
        editor.set(createEditor(worldPath))
    }

    private fun createEditor(worldPath: Path): Editor {
        val wwd = loadWwd(Files.newInputStream(worldPath))
        return Editor(wwd, rezIndex, worldPath)
    }

    private fun createEditor(baseLevel: BaseLevel): Editor {
        TODO()
    }
}
