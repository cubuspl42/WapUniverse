package wapuniverse.editor

import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Vec2i

class TileModeContext(val plane: Plane) : ModeContext() {
    val tileCursor = newSimpleVar(Vec2i(0, 0))

    val tileId = newSimpleVar(12)

    fun insertTile() {
        check(!isDisposed)
        plane.putTile(tileCursor.value, tileId.value)
        tileCursor.value += Vec2i(1, 0)
    }
}
