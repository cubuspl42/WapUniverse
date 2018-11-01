package wapuniverse.view

import javafx.geometry.BoundingBox
import javafx.scene.canvas.GraphicsContext
import javafx.scene.transform.Affine
import javafx.scene.transform.Translate
import wapuniverse.model.Plane
import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider

class PlaneRenderer(
        private val rezImageProvider: RezImageProvider,
        private val graphicsContext: GraphicsContext,
        private val bounds: BoundingBox
) {
    fun renderPlane(planeEditor: PlaneEditor) {
        val plane = planeEditor.plane
        val translate = planeEditor.cameraOffset.value.toPoint2D()

        graphicsContext.translate(-translate.x, -translate.y)
//        graphicsContext.transform = Affine(Translate(-translate.x, -translate.y))
        renderPlaneContent(plane)
    }

    private fun renderPlaneContent(plane: Plane) {
        val transformedBounds = graphicsContext.transform.inverseTransform(bounds)
        plane.tiles.forEach { offset, tileId ->
            val image = plane.findTileImageMetadata(tileId)?.let {
                rezImageProvider.provideImage(it.rezPath)
            }
            val tileBounds = BoundingBox(offset.x * 64.0, offset.y * 64.0, 64.0, 64.0)
            if (transformedBounds.intersects(tileBounds)) {
                graphicsContext.drawImage(image, tileBounds.minX, tileBounds.minY)
            }
        }
        plane.wapObjects.forEach { wapObject ->
            val image = wapObject.imageMetadata.value?.let {
                rezImageProvider.provideImage(it.rezPath)
            }
            if (image != null) {
                val objectBounds = wapObject.bounds.value!!
                if (transformedBounds.intersects(objectBounds)) {
                    graphicsContext.drawImage(image, objectBounds.minX, objectBounds.minY)
                }
            }
        }
    }
}
