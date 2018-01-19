package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.selectToolContext
import wapuniverse.view.ext.attachController
import wapuniverse.view.ext.position

class SelectionSurfaceController(
        private val surface: Node,
        private val selectToolContext: SelectToolContext
) : Controller {
    private val onMousePressed = EventHandler<MouseEvent> { ev ->
        val areaSelectionContext = selectToolContext.selectObjectsByArea(ev.position)
        AreaSelectionController(surface, areaSelectionContext)
    }

    init {
        surface.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed)
    }

    override fun uninit() {
        surface.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed)
    }
}
