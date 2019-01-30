package wapuniverse.app

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Dialog
import wapuniverse.editor.Retail
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType

class NewWorldDialog {
    private val dialog = Dialog<NewWorldParams?>()

    val retail = SimpleObjectProperty<Retail>(Retail.LA_ROCA)

    init {
        val createButtonType = ButtonType(createButtonText, ButtonData.OK_DONE)

        dialog.dialogPane.apply {
            content = newWorldDialogUi(this@NewWorldDialog)
            buttonTypes.addAll(createButtonType, ButtonType.CANCEL)
        }

        dialog.setResultConverter {
            if (it != createButtonType) null
            else NewWorldParams(retail.value)
        }
    }

    fun showAndWait(): NewWorldParams? = dialog.showAndWait().orElse(null)
}
