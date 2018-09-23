package wapuniverse.model.mainwindow

import wapuniverse.model.Editor
import wapuniverse.view.extensions.map

class EditorContext(
        private val editor: Editor
) {
    val planeEditorContext = editor.planeEditor.map { PlaneEditorContext(it) }

    init {
    }
}
