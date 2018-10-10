package wapuniverse.view

import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import org.reactfx.EventStreams.merge
import wapuniverse.model.PencilToolContext
import wapuniverse.model.PlaneEditor
import wapuniverse.view.extensions.forEach
import wapuniverse.view.extensions.point

class WorldViewPencilToolContextController(
        planeEditor: PlaneEditor,
        wrapperPane: Pane,
        pencilToolContext: PencilToolContext
) : Controller(pencilToolContext) {
    init {
        subscribe(merge(
                eventsOf(wrapperPane, MouseEvent.MOUSE_MOVED),
                eventsOf(wrapperPane, MouseEvent.MOUSE_DRAGGED)
        )) { event ->
            val point = planeEditor.cameraToWorld(event.point.toVec2i())
            pencilToolContext.setCursorOffset(point / 64)
        }

        subscribe(eventsOf(wrapperPane, MouseEvent.MOUSE_EXITED)) {
            pencilToolContext.setCursorOffset(null)
        }

        subscribe(eventsOf(wrapperPane, MouseEvent.MOUSE_PRESSED)) { event ->
            if (event.button == MouseButton.PRIMARY) {
                pencilToolContext.startDrawing()
            }
        }

        pencilToolContext.drawingContext.forEach {
            WorldViewDrawingContextController(it, wrapperPane)
        }
    }
}
