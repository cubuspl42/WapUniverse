package wapuniverse.app

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.editor.World

class RootWindow(
        stage: Stage
) {
    private val contextVar = SimpleObjectProperty<RootWindowContext>()

    val context = contextVar as ObservableValue<RootWindowContext?>

    init {
        stage.apply {
            title = rootWindowTitle
            scene = Scene(rootWindowUi(this@RootWindow))
            show()
        }
    }

    fun newWorld() {
        val params = NewWorldDialog().showAndWait() ?: return
        enterEditorContext(params)
    }

    private fun enterEditorContext(newWorldParams: NewWorldParams) {
        contextVar.value = EditorContext(World(newWorldParams.retail))
    }
}
