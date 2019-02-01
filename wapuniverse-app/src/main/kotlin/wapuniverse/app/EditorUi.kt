package wapuniverse.app

import javafx.beans.value.ObservableValue
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import wapuniverse.editor.extensions.map

fun editorMainViewUi(editorContext: EditorContext) = HBox(
        ImageView(editorContext.image!!),
        text(editorContext.editor.activePlane.map { it.name })
)

private fun text(textValue: ObservableValue<String>) = Text().apply {
    textProperty().bind(textValue)
}
