package wapuniverse.editor

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import org.reactfx.value.Val
import org.reactfx.value.Var
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.editor.extensions.map
import wapuniverse.editor.util.Disposable
import wapuniverse.editor.util.disposeOldValues
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Size2i
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import java.lang.IllegalStateException

enum class CameraMovementDirection {
    LEFT, UP, DOWN, RIGHT
}

private val cameraDelta = 64

class ActivePlaneContext(private val editor: Editor, val plane: Plane) : Disposable() {
    private val cameraPositionVar = SimpleObjectProperty(Vec2d())

    val cameraPosition = cameraPositionVar as ObservableValue<Vec2d>

    val cameraSize = newSimpleVar(Size2i())!!

    val cameraRect = Val.combine(cameraPosition.map { it.toVec2i() }, cameraSize, ::Rect2i)

    fun moveCamera(direction: CameraMovementDirection) {
        val delta = when (direction) {
            CameraMovementDirection.LEFT -> Vec2i(-cameraDelta, 0)
            CameraMovementDirection.UP -> Vec2i(0, -cameraDelta)
            CameraMovementDirection.DOWN -> Vec2i(0, cameraDelta)
            CameraMovementDirection.RIGHT -> Vec2i(cameraDelta, 0)
        }
        scrollCamera(delta.toVec2d())
    }

    fun scrollCamera(delta: Vec2d) {
        cameraPositionVar.value += delta
    }

    val modeContext = editor.mode.map {
        when(it) {
            Mode.OBJECT -> ObjectModeContext(this, plane)
            Mode.TILE -> TileModeContext(plane)
            else -> throw IllegalStateException()
        }
    }!!

    val objectModeContext = modeContext.map { it as? ObjectModeContext }!!

    val tileModeContext = modeContext.map { it as? TileModeContext }!!
}
