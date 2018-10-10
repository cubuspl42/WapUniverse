package wapuniverse.view

import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams
import org.reactfx.EventStreams.eventsOf
import wapuniverse.model.DrawingContext

class WorldViewDrawingContextController(
        drawingContext: DrawingContext,
        wrapperPane: Pane
) : Controller(drawingContext){
    init {
        subscribe(eventsOf(wrapperPane, MouseEvent.MOUSE_RELEASED)) { event ->
            if (event.button == MouseButton.PRIMARY) {
                drawingContext.finish()
            }
        }
    }
}
