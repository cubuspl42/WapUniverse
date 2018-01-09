package wapuniverse.view

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.ToolContext
import wapuniverse.view.ext.map
import wapuniverse.view.ext.position

class SelectionSurfaceController(
        private val surface: Node,
        private val selectToolContext: SelectToolContext
) {
    private val onMousePressed = EventHandler<MouseEvent> { ev ->
        val areaSelectionContext = selectToolContext.selectObjectsByArea(ev.position)
        AreaSelectionController(surface, areaSelectionContext)
    }

    init {
        surface.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed)
    }

    fun uninit() {
        surface.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed)
    }
}

fun attachSelectionSurfaceController(surface: Node, editorContext: EditorContext) {
    editorContext.activeToolContext.map({ toolContext: ToolContext ->
        (toolContext as? SelectToolContext)?.let {
            SelectionSurfaceController(surface, it)
        }
    }, { it?.uninit() })
}