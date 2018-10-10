package wapuniverse.view.util

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind
import wapuniverse.view.extensions.subscribe

private val childKey = "childKey"

fun group(children: ObservableList<out Node>) = Group().apply {
    this.properties[children] = children
    EasyBind.listBind(this.children, children)
}

fun group(child: ObservableValue<Node?>): Group {
    return Group().apply {
        properties[childKey] = child
    }.also { group ->
        child.subscribe { node ->
            node?.let { group.children.setAll(it) }
        }
    }
}
