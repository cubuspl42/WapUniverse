package wapuniverse.editor

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import kotlin.math.min

class AreaSelectionContext(
        parent: Disposable,
        position: ObservableValue<Vec2i>
) : Disposable(parent) {
    private val startPosition = position.value

    val area = position.map {
        val ev = EucVec2i(startPosition, it)
        BoundingBox(
                min(ev.a.x, ev.b.x).toDouble(),
                min(ev.a.y, ev.b.y).toDouble(),
                ev.delta().width.toDouble(),
                ev.delta().height.toDouble()
        )
    }

    fun commit() {
        dispose()
    }
}
