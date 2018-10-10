package wapuniverse.model

import javafx.beans.value.ObservableValue
import org.reactfx.EventStreams.nonNullValuesOf
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.Disposable

private const val tileId = 12

class DrawingContext(
        private val plane: Plane,
        cursorOffset: ObservableValue<Vec2i?>
) : Disposable() {
    private val backup = mutableSetOf<Pair<Vec2i, Int>>()

    init {
        subscribe(nonNullValuesOf(cursorOffset)) {
            backup.add(it!! to plane.getTile(it))
            plane.setTile(it, tileId)
        }
    }

    fun abort() {
        restoreBackup()
        dispose()
    }

    fun finish() {
        dispose()
    }

    private fun restoreBackup() {
        backup.forEach { (offset, tileId) -> plane.setTile(offset, tileId) }
    }
}
