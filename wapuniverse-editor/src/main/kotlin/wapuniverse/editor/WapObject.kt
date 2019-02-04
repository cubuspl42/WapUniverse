package wapuniverse.editor

import javafx.geometry.BoundingBox
import org.reactfx.value.Val
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Vec2i

class WapObject(
        val position: Vec2i,
        val imageSet: String
) {
    private val isHighlightedVar = newSimpleVar(false)

    val isHighlighted = isHighlightedVar as Val<Boolean>

    fun highlight() {
        isHighlightedVar.value = true
    }

    fun unhighlight() {
        isHighlightedVar.value = false
    }

    val size = Vec2i(64, 64)

    val boundingBox = BoundingBox(
            position.x.toDouble(),
            position.y.toDouble(),
            size.width.toDouble(),
            size.height.toDouble()
    )
}
