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

enum class CameraMovementDirection {
    LEFT, UP, DOWN, RIGHT
}

private val cameraDelta = 64

class ActivePlaneContext(val plane: Plane) : Disposable() {
    private val cameraPositionVar = SimpleObjectProperty(Vec2d())

    val cameraPosition = cameraPositionVar as ObservableValue<Vec2d>

    val cameraSize = newSimpleVar(Size2i())!!

    val cameraRect = Val.combine(cameraPosition.map { it.toVec2i() }, cameraSize, ::Rect2i)

    private val areaSelectionContextVar = contextProperty<AreaSelectionContext>()

    val areaSelectionContext = areaSelectionContextVar as Val<AreaSelectionContext?>

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

    fun selectByArea(position: Val<Vec2i>) =
            areaSelectionContextVar.enter(AreaSelectionContext(this, position))

    private val editObjectContextVar = contextProperty<EditObjectContext>()

    val editObjectContext = editObjectContextVar as ObservableValue<EditObjectContext?>

    fun editObject(): EditObjectContext? {
        val wapObject = plane.selectedObjects.firstOrNull() ?: return null
        return editObjectContextVar.enter(EditObjectContext(wapObject))
    }

    fun insertObject() {
        check(!isDisposed)
        plane.insertObject(cameraRect.value.center())
    }

    fun deleteObject() {
        check(!isDisposed)
        plane.removeSelectedObjects()
    }
}

class ContextProperty<T : Disposable>(
        private val property: Var<T?>
) : Val<T?> by property {
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
        ContextProperty<T>(newSimpleVar(null))
