package wapuniverse.view

import javafx.geometry.BoundingBox
import javafx.scene.canvas.Canvas
import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider

fun planeCanvas(planeEditor: PlaneEditor, rezImageProvider: RezImageProvider): Canvas =
        ResizableCanvas(planeEditor) { width, height, graphicsContext ->
            val bounds = BoundingBox(0.0, 0.0, width, height)
            PlaneRenderer(rezImageProvider, graphicsContext, bounds).renderPlane(planeEditor)
        }