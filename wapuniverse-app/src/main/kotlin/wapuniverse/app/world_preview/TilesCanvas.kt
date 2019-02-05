package wapuniverse.app.world_preview

import javafx.scene.canvas.Canvas
import wapuniverse.editor.Plane
import wapuniverse.editor.emptyTile
import wapuniverse.rez.RezImageCache

val tileLength = 64

class TilesCanvas(plane: Plane, rezImageCache: RezImageCache) : Canvas() {
    private val tiles = plane.tiles

    private val tilesImages = rezImageCache
            .imageSets[plane.fqTilesetId]
            ?.getAllImages()
            ?.mapValues { it.value?.image }

    init {
        width = 1024.0
        height = 1024.0

        draw()
        // FIXME: leak (plane / activePlaneContext)
        tiles.changes().subscribe { draw() }
    }

    private fun draw() {
        tiles.forEach { i: Int, j: Int, tileId: Int ->
            if (tileId != emptyTile) {
                tilesImages?.get(tileId)?.let { image ->
                    val x = j * tileLength.toDouble()
                    val y = i * tileLength.toDouble()
                    graphicsContext2D.drawImage(image, x, y)
                }
            }
        }
    }
}
