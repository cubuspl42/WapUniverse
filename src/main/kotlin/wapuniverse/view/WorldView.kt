package wapuniverse.view

import javafx.scene.Node
import javafx.scene.text.Text
import wapuniverse.model.Plane
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group

fun worldView(plane: Plane): Node {
    val tileNodes = plane.tiles.toObservableList { index, tileId ->
        Text().apply {
            x = index.x * 64.0
            y = index.y * 64.0
            text = tileId.toString()
        }
    }
    return group(tileNodes)
}
