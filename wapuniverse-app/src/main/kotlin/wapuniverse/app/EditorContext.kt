package wapuniverse.app

import javafx.stage.FileChooser
import javafx.stage.Stage
import wapuniverse.editor.Editor
import wapuniverse.editor.World
import wapuniverse.editor.extensions.forEach
import wapuniverse.rez.RezImageCache
import java.nio.file.Files
import java.nio.file.Path

class EditorContext(
        private val rootWindow: RootWindow,
        private val world: World,
        val rezImageCache: RezImageCache
) : RootWindowContext() {
    private val stage = rootWindow.stage

    val editor = Editor(world)

    fun saveWorld() {
        val path = retrieveWorldSavePath(stage) ?: return
        saveWorld(world, path)
    }

    fun editWorld() {
        WorldDialog(editor)
    }

    fun editPlanes() {
        PlanesDialog(editor)
    }

    init {
        editor.activePlaneContext
                .flatMap { it!!.objectModeContext }
                .flatMap { it!!.editObjectContext }
                .forEach { EditObjectDialog(it) }
    }
}

private fun retrieveWorldSavePath(parent: Stage): Path? {
    val fileChooser = FileChooser().apply {
        title = "Save World"
    }

    val file = fileChooser.showSaveDialog(parent)

    return file?.toPath()
}

private fun saveWorld(world: World, path: Path) {
    Files.newOutputStream(path).use {
        world.save(it)
    }
}