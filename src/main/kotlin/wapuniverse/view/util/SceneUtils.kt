package wapuniverse.view.util

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind

fun group(children: ObservableList<out Node>) = Group().apply {
    this.properties[children] = children
    EasyBind.listBind(this.children, children)
}
