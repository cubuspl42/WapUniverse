package wapuniverse.app

import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane

const val rootWindowTitle = "WapUniverse"

fun rootWindowUi(): Parent {
    val menuBar = MenuBar(
            Menu("File", null,
                    MenuItem("New"),
                    MenuItem("Open")
            )
    )
    val center = Group()
    return BorderPane().apply {
        top = menuBar
        setCenter(center)
        prefWidth = 640.0
        prefHeight = 480.0
    }
}
