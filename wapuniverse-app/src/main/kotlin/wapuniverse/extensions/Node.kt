package wapuniverse.extensions

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind

fun group(children: ObservableList<out Node>) = Group().apply {
    properties[children] = children
    EasyBind.listBind(this.children, children)
}


fun group(child: ObservableValue<Node?>) = Group().apply {
    properties[child] = child
    listBind(this.children, child)
}
