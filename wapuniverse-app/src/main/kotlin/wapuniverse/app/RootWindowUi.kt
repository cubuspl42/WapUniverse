package wapuniverse.app

import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import wapuniverse.editor.extensions.map

const val rootWindowTitle = "WapUniverse"

fun rootWindowUi(rootWindow: RootWindow): Parent {
    val menuBar = MenuBar(
            Menu("File", null,
                    menuItem("New", rootWindow::newWorld),
                    MenuItem("Open")
            )
    )
    return BorderPane().apply {
        top = menuBar
        centerProperty().bind(rootWindowCenterUi(rootWindow.context))
        prefWidth = 640.0
        prefHeight = 480.0
    }
}

fun rootWindowCenterUi(context: ObservableValue<RootWindowContext?>) = context.map {
    when (it) {
        is EditorContext -> editorMainViewUi(it)
        else -> Group()
    }
}

private inline fun menuItem(text: String, crossinline callback: () -> Unit) =
        MenuItem(text).apply {
            setOnAction { callback() }
        }
