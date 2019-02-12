package wapuniverse.app

import editObjectDialogUi
import javafx.scene.Scene
import javafx.stage.Stage
import org.reactfx.value.Var
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.editor.*

class EditObjectDialog(
        private val editObjectContext: EditObjectContext
) {
    private val wapObject = editObjectContext.wapObject

    private inner class FieldVar<T : Any>(private val attrKey: WapObjectAttrKey<T>) {
        val variable = newSimpleVar(wapObject.getAttr(attrKey).value)!!

        fun sync() {
            editObjectContext.setAttr(attrKey, variable.value)
        }
    }

    private val fieldVars = WapObjectAttrKey.allKeys.map {
        when (it) {
            is WapObjectIntAttrKey -> it to fieldVar(it)
            is WapObjectStringAttrKey -> it to fieldVar(it)
        }
    }.toMap()

    private fun <T : Any> fieldVar(attrKey: WapObjectAttrKey<T>) =
            FieldVar(attrKey)

    fun <T : Any> getFieldVar(it: WapObjectAttrKey<T>): Var<T> =
            uncheckedCast(fieldVars.getValue(it).variable)

    init {
        Stage().apply {
            title = "Edit Object"
            scene = Scene(editObjectDialogUi(this@EditObjectDialog))
            show()
        }
    }

    fun save() {
        editObjectContext.commit()
    }
}
