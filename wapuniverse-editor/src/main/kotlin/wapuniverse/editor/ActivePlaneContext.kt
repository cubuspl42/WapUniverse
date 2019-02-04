package wapuniverse.editor

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import org.reactfx.value.Val
import wapuniverse.editor.util.Disposable
import wapuniverse.editor.util.disposeOldValues
import wapuniverse.geom.Vec2i

enum class CameraMovementDirection {
    LEFT, UP, DOWN, RIGHT
}

private val cameraDelta = 64

class ActivePlaneContext(val plane: Plane) : Disposable() {
    private val cameraPositionVar = SimpleObjectProperty<Vec2i>(Vec2i())

    val cameraPosition = cameraPositionVar as ObservableValue<Vec2i>

    private val areaSelectionContextVar = contextProperty<AreaSelectionContext>()

    val areaSelectionContext = areaSelectionContextVar as ObservableValue<AreaSelectionContext?>

    fun moveCamera(direction: CameraMovementDirection) {
        val delta = when (direction) {
            CameraMovementDirection.LEFT -> Vec2i(-cameraDelta, 0)
            CameraMovementDirection.UP -> Vec2i(0, -cameraDelta)
            CameraMovementDirection.DOWN -> Vec2i(0, cameraDelta)
            CameraMovementDirection.RIGHT -> Vec2i(cameraDelta, 0)
        }
        cameraPositionVar.value += delta
    }

    fun selectByArea(position: Val<Vec2i>) =
            areaSelectionContextVar.enter(AreaSelectionContext(this, position))
}

class ContextProperty<T : Disposable>(
        private val property: SimpleObjectProperty<T?>
) : ObservableValue<T?> by property {
    fun enter(context: T): T {
        disposeOldValues()
        property.value = context
        context.onDisposed.subscribe { property.value = null }
        return context
    }

    fun reset() {
        property.value = null
    }
}

fun <T : Disposable> contextProperty() =
        ContextProperty<T>(SimpleObjectProperty())
