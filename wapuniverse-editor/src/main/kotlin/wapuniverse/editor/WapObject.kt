package wapuniverse.editor

import io.github.jwap32.v1.WwdObject
import org.reactfx.value.Val
import org.reactfx.value.Val.combine
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i

class WapObject(
        val plane: Plane,
        wwdObject: WwdObject
) {
    private val world = plane.world

    private val positionVar = newSimpleVar(Vec2i(wwdObject.x, wwdObject.y))

    val position = positionVar as Val<Vec2i>

    val i = wwdObject.i

    private val imageSetVar = newSimpleVar(wwdObject.imageSet)

    val imageSet = imageSetVar as Val<String>

    val fqImageSetId = imageSet.map { world.expandImageSetId(it) }

    val imageMetadata = fqImageSetId.map { world.supplyMetadata(it!!, i) }

    private val isHighlightedVar = newSimpleVar(false)

    val isHighlighted = isHighlightedVar as Val<Boolean>

    fun highlight() {
        isHighlightedVar.value = true
    }

    fun unhighlight() {
        isHighlightedVar.value = false
    }

    val boundingBoxLocal = imageMetadata.map {
        Rect2i.fromCenter(it!!.offset, it.size)
    }

    val boundingBox = combine(position, boundingBoxLocal) { positionNow, boundingBoxLocalNow ->
        boundingBoxLocalNow!! + positionNow!!
    }
}

