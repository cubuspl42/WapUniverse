package wapuniverse.app.world_preview

import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.changesOf
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.emptyTile
import wapuniverse.rez.RezImageCache

const val tileLength = 64

class TilesCanvas(
        private val activePlaneContext: ActivePlaneContext,
        rezImageCache: RezImageCache,
        previewPane: Pane
) : Canvas() {
    private val plane = activePlaneContext.plane

    private val tiles = plane.tiles

    private val cameraPosition = activePlaneContext.cameraPosition

    private val tilesImages = rezImageCache
            .imageSets[plane.fqTilesetId]
            ?.getAllImages()
            ?.mapValues { it.value?.image }

    init {
        widthProperty().bind(previewPane.widthProperty())
        heightProperty().bind(previewPane.heightProperty())
        draw()

        tiles.changes()
                .or(changesOf(cameraPosition))
                .or(changesOf(previewPane.widthProperty()))
                .or(changesOf(previewPane.heightProperty()))
                .subscribe { draw() } // FIXME: leak (plane / activePlaneContext)
    }

    private fun draw() {
        graphicsContext2D.save()
        graphicsContext2D.clearRect(0.0, 0.0, width, height)
        val t = -cameraPosition.value.toVec2d()
        graphicsContext2D.translate(t.x, t.y)
        drawTiles()
        graphicsContext2D.restore()
    }

    private fun drawTiles() {
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
