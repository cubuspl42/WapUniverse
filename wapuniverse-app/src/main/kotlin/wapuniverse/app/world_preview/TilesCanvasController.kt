package wapuniverse.app.world_preview

import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.changesOf
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.emptyTile
import wapuniverse.rez.RezImageCache

const val tileLength = 64

class TilesCanvasController(
        activePlaneContext: ActivePlaneContext,
        private val rezImageCache: RezImageCache,
        private val canvas: Canvas,
        previewPane: Pane
) : Controller(activePlaneContext) {
    private val plane = activePlaneContext.plane

    private val tiles = plane.tiles

    private val cameraPosition = activePlaneContext.cameraPosition

    private fun makeTilesImages(fqImageSetId: String) =
            rezImageCache
                    .imageSets[fqImageSetId]
                    ?.getAllImages()
                    ?.mapValues { it.value?.image }

    private val tilesImages = plane.fqImageSetId.map(this::makeTilesImages)

    init {
        canvas.widthProperty().bind(previewPane.widthProperty())
        canvas.heightProperty().bind(previewPane.heightProperty())
        draw()

        val stream = tiles.changes()
                .or(tilesImages.changes())
                .or(changesOf(cameraPosition))
                .or(changesOf(previewPane.widthProperty()))
                .or(changesOf(previewPane.heightProperty()))
        subscribe(stream) { draw() }
    }

    private fun draw() {
        canvas.graphicsContext2D.run {
            save()
            clearRect(0.0, 0.0, canvas.width, canvas.height)
            val t = -cameraPosition.value
            translate(t.x, t.y)
            drawTiles()
            restore()
        }
    }

    private fun drawTiles() {
        tiles.forEach { i: Int, j: Int, tileId: Int ->
            if (tileId != emptyTile) {
                tilesImages.value?.get(tileId)?.let { image ->
                    val x = j * tileLength.toDouble()
                    val y = i * tileLength.toDouble()
                    canvas.graphicsContext2D.drawImage(image, x, y)
                }
            }
        }
    }
}
