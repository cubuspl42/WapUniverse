package wapuniverse.app

import javafx.scene.image.ImageView

fun editorMainViewUi(editorContext: EditorContext): ImageView {
    return ImageView(editorContext.image!!)
}
