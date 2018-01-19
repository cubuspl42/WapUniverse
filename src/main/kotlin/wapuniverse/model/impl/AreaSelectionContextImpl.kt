package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2d
import wapuniverse.model.AreaSelection
import wapuniverse.model.AreaSelectionContext
import wapuniverse.view.ext.map

class AreaSelectionContextImpl(
        private val startPoint: Vec2d,
        private val areaSelection: SimpleObjectProperty<AreaSelectionImpl>,
        private val selectToolContext: SelectToolContextImpl,
        private val plane: PlaneImpl
) : AreaSelectionContext {
    private var closed = false

    private val endPoint = SimpleObjectProperty<Vec2d>(startPoint)

    init {
        val selectionBounds = endPoint.map { endPoint ->
            val delta = endPoint - startPoint
            BoundingBox(startPoint.x, startPoint.y, delta.x, delta.y)
        }
        areaSelection.set(AreaSelectionImpl().apply {
            boundingBox.bind(selectionBounds)
            preselectedObjects.bind(selectionBounds.map { selectionBoundsVal ->
                plane.objectsIntersecting(selectionBoundsVal)
            })
        })
    }

    override fun setEndPoint(endPoint: Vec2d) {
        if (closed) throw IllegalStateException()
        this.endPoint.set(endPoint)
    }

    override fun commit() {
        if (closed) throw IllegalStateException()
        selectToolContext.selectPreselectedObjects()
        areaSelection.set(null)
        closed = true
    }
}
