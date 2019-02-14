package wapuniverse.app

import editObjectDialogUi
import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.editor.EditObjectContext
import wapuniverse.editor.WapObjectIntAttrKey
import wapuniverse.editor.WapObjectStringAttrKey

class EditObjectDialog(
        private val editObjectContext: EditObjectContext
) {
    private val window = Stage().apply {
        title = "Edit Object"
        scene = Scene(editObjectDialogUi(this@EditObjectDialog))
        setOnCloseRequest { editObjectContext.abort() }
        show()
    }

    fun getVar(attrKey: WapObjectIntAttrKey) = editObjectContext.editAttr(attrKey)

    fun getVar(attrKey: WapObjectStringAttrKey) = editObjectContext.editAttr(attrKey)

    init {
        editObjectContext.onDisposed.subscribe { window.close() }
    }

    fun cancel() {
        editObjectContext.abort()
    }

    fun submit() {
        editObjectContext.commit()
    }
}
