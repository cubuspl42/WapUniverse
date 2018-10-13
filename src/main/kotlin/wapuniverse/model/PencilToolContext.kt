package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i
import wapuniverse.util.objectProperty
import wapuniverse.util.optionalProperty

private const val defaultTileId = 12

class PencilToolContext(
        private val plane: Plane
) : ToolContext() {
    val cursorOffset: ObservableValue<Vec2i?>

    val tileId: ObservableValue<Int>

    val drawingContext: ObservableValue<DrawingContext?>

    private val mCursorOffset = objectProperty<Vec2i>()

    private val mTileId = objectProperty(defaultTileId)

    private val mDrawingContext = optionalProperty<DrawingContext?>()

    init {
        cursorOffset = mCursorOffset
        tileId = mTileId
        drawingContext = mDrawingContext
    }

    fun setCursorOffset(offset: Vec2i?) {
        mCursorOffset.value = offset
    }

    fun setTileId(tileId: Int) {
        mTileId.value = tileId
    }

    fun startDrawing() {
        mDrawingContext.set(DrawingContext(plane, cursorOffset, tileId.value).apply {
            addDisposeListener { mDrawingContext.clear() }
        })
    }

    override fun uninit() {
    }
}
