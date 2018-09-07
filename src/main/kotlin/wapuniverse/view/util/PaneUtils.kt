package wapuniverse.view.util

import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle

fun hideOverflow(pane: Pane) {
    pane.clip = Rectangle().apply {
        widthProperty().bind(pane.widthProperty())
        heightProperty().bind(pane.heightProperty())
    }
}

fun pane(child: ObservableValue<Node>): Pane = BorderPane().apply {
    centerProperty().bind(child)
}
