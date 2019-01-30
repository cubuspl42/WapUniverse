package wapuniverse.app

import javafx.beans.property.Property
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import wapuniverse.editor.Retail

const val createButtonText = "Create"

fun newWorldDialogUi(newWorldDialog: NewWorldDialog) = twoColumnGrid(
        Label("Retail:") to comboBox(Retail.values(), newWorldDialog.retail)
)

private fun twoColumnGrid(vararg rows: Pair<Node, Node>) = GridPane().apply {
    rows.forEachIndexed { i, (l, r) ->
        addRow(i, l, r)
    }
}

private fun <T> comboBox(values: Array<T>, property: Property<T>) =
        ComboBox<T>().apply {
            items.setAll(*values)
            valueProperty().bindBidirectional(property)
        }
