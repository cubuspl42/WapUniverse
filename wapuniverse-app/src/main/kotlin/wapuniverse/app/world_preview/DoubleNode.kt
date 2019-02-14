package wapuniverse.app.world_preview

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import net.corda.client.jfx.utils.map
import wapuniverse.extensions.group

class DoubleNode(
        val backNode: Node,
        val frontNode: Node
)

fun doubleGroup(children: ObservableList<DoubleNode>) = Group(
        group(children.map { it.backNode }),
        group(children.map { it.frontNode })
)
