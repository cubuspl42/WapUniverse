package wapuniverse.editor

import javafx.beans.value.ObservableValue
import org.reactfx.value.Val
import org.reactfx.value.Var
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.editor.util.Disposable
import wapuniverse.editor.util.disposeOldValues
import wapuniverse.geom.Vec2i

class ObjectModeContext(
        private val activePlaneContext: ActivePlaneContext,
        val plane: Plane
) : ModeContext() {
    private val areaSelectionContextVar = contextProperty<AreaSelectionContext>()

    val areaSelectionContext = areaSelectionContextVar as Val<AreaSelectionContext?>

    fun selectByArea(position: Val<Vec2i>) =
            areaSelectionContextVar.enter(AreaSelectionContext(this, position))

    private val editObjectContextVar = contextProperty<EditObjectContext>()

    val editObjectContext = editObjectContextVar as ObservableValue<EditObjectContext?>

    fun editObject() {
        val wapObject = plane.selectedObjects.firstOrNull() ?: return
        editObjectContextVar.enter(EditObjectContext(wapObject))
    }

    fun insertObject() {
        check(!isDisposed)
        plane.insertObject(activePlaneContext.cameraRect.value.center())
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
