package wapuniverse.view.canvasscene

import javafx.collections.ObservableSet
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import org.reactfx.EventStream
import org.reactfx.EventStreams.invalidationsOf
import org.reactfx.EventStreams.merge
import wapuniverse.model.Plane
import wapuniverse.model.PlaneEditor
import wapuniverse.model.WapObject
import wapuniverse.model.util.Disposable
import wapuniverse.rez.RezImageProvider
import wapuniverse.util.map
import wapuniverse.view.ResizableCanvas
import wapuniverse.view.extensions.map

class CanvasScene(
        parent: Disposable,
        private val root: CanvasNode
) : Pane() {
    private val canvas = ResizableCanvas { width, height, graphicsContext ->
        root.draw(graphicsContext)
    }

    private val sub = root.invalidations.subscribe {
        canvas.redraw()
    }

    init {
        canvas.widthProperty().bind(widthProperty())
        canvas.heightProperty().bind(heightProperty())
        children.add(canvas)
        parent.addDisposeListener { sub.unsubscribe() }
    }
}

abstract class CanvasNode {
    abstract val invalidations: EventStream<Void?>

    internal abstract fun draw(graphicsContext: GraphicsContext)
}

class PlaneNode(
        private val planeEditor: PlaneEditor,
        private val children: ObservableSet<CanvasNode>
) : CanvasNode() {
    override val invalidations =
            merge(
                    invalidationsOf(planeEditor.cameraOffset),
                    merge(children.map { it.invalidations })!!
            )!!

    override fun draw(graphicsContext: GraphicsContext) {
        graphicsContext.save()
        val t = -planeEditor.cameraOffset.value.toVec2d()
        graphicsContext.translate(t.x, t.y)

        graphicsContext.fill = Color.RED
        graphicsContext.fillRect(0.0, 0.0, 64.0, 64.0)

        children.forEach { it.draw(graphicsContext) }
        graphicsContext.restore()
    }

}

class TileMapNode(
        private val plane: Plane,
        private val rezImageProvider: RezImageProvider
) : CanvasNode() {
    override val invalidations = plane.tiles.changes.map { null as Void? }!!

    override fun draw(graphicsContext: GraphicsContext) {
        plane.tiles.forEach { tileOffset, tileId ->
            val image = plane.findTileImageMetadata(tileId)?.let {
                rezImageProvider.provideImage(it.rezPath)
            }
            graphicsContext.drawImage(image, tileOffset.x * 64.0, tileOffset.y * 64.0)
        }
    }
}

class WapObjectNode(
        private val wapObject: WapObject,
        private val rezImageProvider: RezImageProvider
) : CanvasNode() {
    private val image =
            wapObject.imageMetadata.map { rezImageProvider.provideImage(it.rezPath) }

    override val invalidations = merge(
            invalidationsOf(wapObject.bounds),
            invalidationsOf(image)
    )!!

    override fun draw(graphicsContext: GraphicsContext) {
        image.value?.let { image ->
            val objectBounds = wapObject.bounds.value!!
            graphicsContext.drawImage(image, objectBounds.minX, objectBounds.minY)
        }
    }
}