package wapuniverse.view

import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.scene.CacheHint
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import wapuniverse.geom.Vec2i
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.observableValue

class TilesController(
        private val tilesGroup: Group,
        tiles: ObservableMap<Vec2i, Int>,
        private val rezImageProvider: RezImageProvider
) {
    private val nodeMap = mutableMapOf<Vec2i, Node>()

    init {
        tiles.forEach { index, tileId ->
            showTile(index, tileId)
        }

        tiles.addListener { change: MapChangeListener.Change<out Vec2i, out Int> ->
            val index = change.key
            if (change.wasAdded()) {
                val tileId = change.valueAdded
                showTile(index, tileId)
            } else if (change.wasRemoved()) {
                destroyTile(index)
            }
        }
    }

    private fun showTile(index: Vec2i, tileId: Int) {
        val image = observableValue {
            rezImageProvider.provideImage("LEVEL1_TILES_ACTION", tileId)!!.image
        }
        val node = if (tileId >= 0) {
            ImageView().apply {
                x = index.x * 64.0
                y = index.y * 64.0
                imageProperty().bind(image)
            }
        } else Group()
        nodeMap[index] = node
        tilesGroup.children.add(node)
    }

    private fun destroyTile(index: Vec2i) {
        val node = nodeMap[index]
        tilesGroup.children.remove(node)
    }
}