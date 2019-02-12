package wapuniverse.util

import javafx.scene.Node
import javafx.scene.layout.GridPane

fun twoColumnGrid(vararg rows: Pair<Node, Node>) = GridPane().apply {
    rows.forEachIndexed { i, (l, r) ->
        addRow(i, l, r)
    }
}