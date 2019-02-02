package wapuniverse.app.worldPreview

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import wapuniverse.extensions.group
import wapuniverse.extensions.map

class DoubleNode(
        val backNode: Node,
        val frontNode: Node
)

fun doubleGroup(children: ObservableList<DoubleNode>) = Group(
        group(children.map { it.backNode }),
        group(children.map { it.frontNode })
)
