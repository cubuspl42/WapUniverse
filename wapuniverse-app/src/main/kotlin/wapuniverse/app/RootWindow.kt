package wapuniverse.app

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.app.world_preview.WorldPreviewPresenter
import wapuniverse.editor.World
import wapuniverse.editor.extensions.flatMap
import wapuniverse.editor.extensions.flatMapOl
import wapuniverse.rez.RezImageCache

class RootWindow(
        stage: Stage,
        private val rezImageCache: RezImageCache
) {
    private val worldPreviewPresenter = WorldPreviewPresenter(rezImageCache)

    private val rootWindowPresenter = RootWindowPresenter(worldPreviewPresenter)

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

    private fun enterEditorContext(newWorldParams: NewWorldParams) {
        contextVar.value = EditorContext(
                World(newWorldParams.retail, ImageMetadataSupplierImpl(rezImageCache)),
                rezImageCache
        )
    }
}
