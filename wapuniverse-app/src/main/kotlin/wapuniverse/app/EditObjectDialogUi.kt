import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.reactfx.value.Var
import wapuniverse.app.EditObjectDialog
import wapuniverse.editor.WapObjectAttrKey
import wapuniverse.editor.WapObjectIntAttrKey
import wapuniverse.editor.WapObjectStringAttrKey
import wapuniverse.editor.extensions.map
import wapuniverse.util.twoColumnGrid

private val rowDefs = WapObjectAttrKey.allKeys.map {
    it.toString() to it
}

fun editObjectDialogUi(editObjectDialog: EditObjectDialog) =
        twoColumnGrid(*rowDefs.map { (labelText, attrKey) ->
            Label(labelText) to attrTextField(editObjectDialog, attrKey)
        }.toTypedArray())

fun attrTextField(editObjectDialog: EditObjectDialog, attrKey: WapObjectAttrKey<*>) =
        when (attrKey) {
            is WapObjectIntAttrKey -> intTextField(editObjectDialog.getFieldVar(attrKey))
            is WapObjectStringAttrKey -> stringTextField(editObjectDialog.getFieldVar(attrKey))
        }

fun intTextField(intVar: Var<Int>): TextField {
    return TextField().apply {
        textProperty().value = intVar.value.toString()
        intVar.bind(textProperty().map { it.toInt() })
    }
}

fun stringTextField(intVar: Var<String>): TextField {
    return TextField().apply {
        textProperty().value = intVar.value.toString()
        intVar.bind(textProperty())
    }
}
