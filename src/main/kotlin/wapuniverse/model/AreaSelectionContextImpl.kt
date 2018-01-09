package wapuniverse.model

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2d
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.model.impl.WorldImpl
import wapuniverse.view.ext.map

class AreaSelectionContextImpl(
        private val startPoint: Vec2d,
        private val areaSelection: SimpleObjectProperty<AreaSelection>,
        private val editorContext: EditorContextImpl,
        private val world: WorldImpl
) : AreaSelectionContext {
    private val endPoint = SimpleObjectProperty<Vec2d>(startPoint)

    init {
        val selectionBounds = endPoint.map { endPoint ->
            val delta = endPoint - startPoint
            BoundingBox(startPoint.x, startPoint.y, delta.x, delta.y)
        }
        areaSelection.set(AreaSelectionImpl().apply {
            boundingBox.bind(selectionBounds)
            preselectedObjects.bind(selectionBounds.map { selectionBoundsVal ->
                world.objectsIntersecting(selectionBoundsVal)
            })
        })
    }

    override fun setEndPoint(endPoint: Vec2d) {
        this.endPoint.set(endPoint)
    }

    override fun commit() {
        editorContext.selectPreselectedObjects()
        areaSelection.set(null)
    }
}
