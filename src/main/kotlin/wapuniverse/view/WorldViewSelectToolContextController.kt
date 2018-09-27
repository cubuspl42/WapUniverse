package wapuniverse.view

import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import wapuniverse.model.PlaneEditor
import wapuniverse.model.SelectToolContext
import wapuniverse.view.extensions.point

class WorldViewSelectToolContextController(
        wrapperPane: Pane,
        planeEditor: PlaneEditor,
        selectToolContext: SelectToolContext
) : Controller(selectToolContext) {
    init {
        subscribe(eventsOf(wrapperPane, MouseEvent.MOUSE_CLICKED)) { event ->
            if (event.button == MouseButton.PRIMARY) {
                selectToolContext.selectObjects(planeEditor.cameraToWorld(event.point.toVec2i()))
            }
        }
    }
}
