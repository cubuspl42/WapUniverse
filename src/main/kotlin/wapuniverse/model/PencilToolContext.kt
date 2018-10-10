package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty
import wapuniverse.util.optionalProperty

class PencilToolContext(
        private val plane: Plane
) : ToolContext() {
    val cursorOffset: ObservableValue<Vec2i?>

    val drawingContext: ObservableValue<DrawingContext?>

    private val mCursorOffset = objectProperty<Vec2i>()

    private val mDrawingContext = optionalProperty<DrawingContext?>()

    init {
        cursorOffset = mCursorOffset
        drawingContext = mDrawingContext
    }

    fun setCursorOffset(offset: Vec2i?) {
        mCursorOffset.value = offset
    }

    fun startDrawing() {
        mDrawingContext.set(DrawingContext(plane, cursorOffset).apply {
            addDisposeListener { mDrawingContext.clear() }
        })
    }

    override fun uninit() {
    }
}
