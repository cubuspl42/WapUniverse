package wapuniverse.util

import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

fun vBox(vararg children: Node, prefWidth: Double) =
        VBox(*children).apply {
            this.prefWidth = prefWidth
        }

fun twoColumnGrid(vararg rows: Pair<Node, Node>) = GridPane().apply {
    rows.forEachIndexed { i, (l, r) ->
        addRow(i, l, r)
    }
}

fun twoColumnGrid(rows: List<Pair<Node, Node>>) = GridPane().apply {
    rows.forEachIndexed { i, (l, r) ->
        addRow(i, l, r)
    }
}

fun twoColumnGrid(vararg rows: Pair<Node, Node>, column0PercentWidth: Double, column1PercentWidth: Double) =
        twoColumnGrid(rows.asIterable(), column0PercentWidth, column1PercentWidth)

fun twoColumnGrid(
        rows: Iterable<Pair<Node, Node>>,

        column0PercentWidth: Double,
        column1PercentWidth: Double
) = GridPane().apply {
    columnConstraints.addAll(
            ColumnConstraints().apply { percentWidth = column0PercentWidth },
            ColumnConstraints().apply { percentWidth = column1PercentWidth }
    )
    rows.forEachIndexed { i, (l, r) -> addRow(i, l, r) }
}
