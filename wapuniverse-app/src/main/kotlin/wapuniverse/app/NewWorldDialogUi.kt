package wapuniverse.app

import javafx.beans.property.Property
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import wapuniverse.editor.Retail
import wapuniverse.util.twoColumnGrid

const val createButtonText = "Create"

fun newWorldDialogUi(newWorldDialog: NewWorldDialog) = twoColumnGrid(
        Label("Retail:") to comboBox(Retail.values(), newWorldDialog.retail)
)

private fun <T> comboBox(values: Array<T>, property: Property<T>) =
        ComboBox<T>().apply {
            items.setAll(*values)
            valueProperty().bindBidirectional(property)
        }
