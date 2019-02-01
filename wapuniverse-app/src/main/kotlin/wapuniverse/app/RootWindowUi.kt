package wapuniverse.app

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import wapuniverse.editor.extensions.map

const val rootWindowTitle = "WapUniverse"

fun rootWindowUi(rootWindow: RootWindow): Parent {
    return BorderPane().apply {
        top = VBox(
                MenuBar(
                        Menu("File", null,
                                menuItem("New", rootWindow::newWorld),
                                MenuItem("Open")
                        )
                ),
                HBox(comboBox(rootWindow.planes, rootWindow.activePlane) {
                    it.name
                })
        )
        centerProperty().bind(rootWindow.context.map { editorMainViewUi(it) } )
        prefWidth = 640.0
        prefHeight = 480.0
    }
}

private fun <T> comboBox(
        items: ObservableList<T>,
        valueProperty: Property<T>,
        provideText: (item: T) -> String
) = ComboBox<T>(items).apply {
    valueProperty().bindBidirectional(valueProperty)

    setCellFactory {
        object : ListCell<T>() {
            override fun updateItem(item: T?, bln: Boolean) {
                super.updateItem(item, bln)
                item?.let { text = provideText(it) }
            }
        }
    }

    buttonCell = cellFactory.call(null)
}

private inline fun menuItem(text: String, crossinline callback: () -> Unit) =
        MenuItem(text).apply {
            setOnAction { callback() }
        }
