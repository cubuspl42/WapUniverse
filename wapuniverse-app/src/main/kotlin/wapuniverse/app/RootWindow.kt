package wapuniverse.app

import io.github.jwap32.v1.loadWwd
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.Stage
import wapuniverse.app.world_preview.WorldPreviewPresenter
import wapuniverse.editor.World
import wapuniverse.editor.extensions.flatMap
import wapuniverse.editor.extensions.flatMapOl
import wapuniverse.rez.RezImageCache
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class RootWindow(
        private val stage: Stage,
        private val rezImageCache: RezImageCache
) {
    private val worldPreviewPresenter = WorldPreviewPresenter(rezImageCache)

    private val rootWindowPresenter = RootWindowPresenter(worldPreviewPresenter)

    private val imageMetadataSupplier = ImageMetadataSupplierImpl(rezImageCache)

    private val contextVar = SimpleObjectProperty<EditorContext>()

    val context = contextVar as ObservableValue<EditorContext>

    val planes = context.flatMapOl { it.editor.world.planes }

    val activePlane = context.flatMap { it.editor.activePlane }

    init {
        stage.apply {
            title = rootWindowPresenter.title()
            scene = Scene(rootWindowPresenter.root(this@RootWindow))
            show()
        }
    }

    fun newWorld() {
        val params = NewWorldDialog().showAndWait() ?: return
        enterEditorContext(params)
    }

    fun openWorld() {
        val path = retrieveWorldPath(stage) ?: return
        val world = loadWorld(path)
        enterEditorContext(world)
    }

    private fun loadWorld(path: Path): World {
        Files.newInputStream(path).use {
            return loadWorld(it)
        }
    }

    private fun loadWorld(inputStream: InputStream): World {
        val wwd = loadWwd(inputStream)
        return World(wwd, imageMetadataSupplier)
    }

    private fun enterEditorContext(world: World) {
        contextVar.value = EditorContext(world, rezImageCache)
    }

    private fun enterEditorContext(newWorldParams: NewWorldParams) {
        TODO()
//        contextVar.value = EditorContext(
//                World(newWorldParams.retail, ImageMetadataSupplierImpl(rezImageCache)),
//                rezImageCache
//        )
    }
}

private fun retrieveWorldPath(parent: Stage): Path? {
    val fileChooser = FileChooser().apply {
        title = "Open World"
    }

    val file = fileChooser.showOpenDialog(parent)

    return file?.toPath()
}
