package wapuniverse.editor

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Vec2i

class AreaSelectionContext(
        parent: Disposable,
        position: ObservableValue<Vec2i>
) : Disposable(parent) {
    private val startPosition = position.value

    val area = position.map {
        val size = (it - startPosition).toVec2d()
        BoundingBox(
                startPosition.x.toDouble(),
                startPosition.y.toDouble(),
                size.width,
                size.height
        )
    }

    fun commit() {
        dispose()
    }
}
