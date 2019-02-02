package wapuniverse.extensions

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind

fun group(children: ObservableList<out Node>) = Group().apply {
    properties[children] = children
    EasyBind.listBind(this.children, children)
}
