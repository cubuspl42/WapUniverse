package wapuniverse.app

import io.github.jwap32.v1.loadWwd
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.reactfx.value.Val
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.app.world_preview.WorldPreviewPresenter
import wapuniverse.editor.World
import wapuniverse.editor.extensions.flatMapOl
import wapuniverse.editor.extensions.flatMapProp
import wapuniverse.rez.RezImageCache
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

typealias Callback = () -> Unit

class RootWindow(
        private val stage: Stage,
        private val rezImageCache: RezImageCache
) {
    private val worldPreviewPresenter = WorldPreviewPresenter(rezImageCache)

    private val rootWindowPresenter = RootWindowPresenter(worldPreviewPresenter)

    private val imageMetadataSupplier = ImageMetadataSupplierImpl(rezImageCache)

    private val contextVar = newSimpleVar<EditorContext>(null)

    val context = contextVar as Val<EditorContext>

    val editor = context.map { it.editor }

    val planes = context.flatMapOl { it.editor.world.planes }

    private val activePlaneContext = context.flatMap { it.editor.activePlaneContext }!!

    private val objectModeContext = activePlaneContext.flatMap { it.objectModeContext }

    val activePlane = context.flatMapProp { it.editor.activePlane }

    fun newWorld() {
        val params = NewWorldDialog().showAndWait() ?: return
        enterEditorContext(params)
    }

    fun openWorld() {
        val path = retrieveWorldPath(stage) ?: return
        val world = loadWorld(path)
        enterEditorContext(world)
    }


    val switchModes = editor.map { { it!!.switchMode() } }

    val editObject: Val<Callback> = objectModeContext.map { { it!!.editObject() } }

    val insertObject: Val<Callback> = objectModeContext.map { { it!!.insertObject() } }

    val deleteObject: Val<Callback> = objectModeContext.map { { it!!.deleteObject() } }

    init {
        stage.apply {
            title = rootWindowPresenter.title()
            scene = Scene(rootWindowPresenter.root(this@RootWindow))
            show()
        }
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
        contextVar.value = EditorContext(this, world, rezImageCache)
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
