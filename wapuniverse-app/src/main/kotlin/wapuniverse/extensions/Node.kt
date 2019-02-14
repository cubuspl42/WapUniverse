package wapuniverse.extensions

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind
import org.reactfx.value.Val

fun group(children: ObservableList<out Node>) = Group().apply {
    properties[children] = children
    EasyBind.listBind(this.children, children)
}


fun group(child: Val<Node?>) = Group().apply {
    properties[child] = child
    listBind(this.children, child)
}
