package wapuniverse.util

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import org.fxmisc.easybind.EasyBind.listBind
import wapuniverse.extensions.listBind

fun Pane.bindChild(child: ObservableValue<Node?>) {
    properties[child] = child
    listBind(this.children, child)
}

fun pane(child: ObservableValue<Node?>) =
        Pane().apply { bindChild(child) }

fun Pane.bindChildren(children: ObservableList<Node?>) {
    properties[children] = children
    listBind(this.children, children)
}

fun pane(children: ObservableList<Node?>) =
        Pane().apply { bindChildren(children) }

fun fullClip(pane: Pane) = Rectangle().apply {
    widthProperty().bind(pane.widthProperty())
    heightProperty().bind(pane.heightProperty())
}
