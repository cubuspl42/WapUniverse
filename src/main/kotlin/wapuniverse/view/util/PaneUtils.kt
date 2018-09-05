package wapuniverse.view.util

import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle

fun hideOverflow(pane: Pane) {
    pane.clip = Rectangle().apply {
        widthProperty().bind(pane.widthProperty())
        heightProperty().bind(pane.heightProperty())
    }
}
