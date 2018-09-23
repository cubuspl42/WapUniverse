package wapuniverse.model

import io.github.jwap32.v1.WwdObject
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2i
import wapuniverse.util.booleanProperty
import wapuniverse.util.objectProperty

class WapObject(
        val plane: Plane,
        wwdObject: WwdObject
) {
    private val world = plane.world

    val position: ObservableValue<Vec2i>

    val i = wwdObject.i

    val imageSet = wwdObject.imageSet

    val mirrored = wwdObject.drawFlags.mirror

    val inverted = wwdObject.drawFlags.invert

    val imageMetadata = world.findObjectImageMetadata(imageSet, i)

    val imageDiagonal: EucVec2i

    val bounds: Bounds

    val isSelected: ObservableBooleanValue

    internal val iIsSelected = booleanProperty(false)

    private val mPosition = objectProperty(Vec2i(wwdObject.x, wwdObject.y))

    init {
        position = mPosition
        isSelected = iIsSelected
        imageDiagonal = calculateImageDiagonal()
        bounds = createBounds()
    }

    internal fun setPosition(pos: Vec2i) {
        mPosition.value = pos
    }

    private fun calculateImageDiagonal(): EucVec2i {
        val p = position.value
        val md = imageMetadata ?: return EucVec2i(p, p)
        val ev = (EucVec2i(Vec2i(), md.size) - md.size / 2 + md.offset)
        return ev + p
    }

    private fun createBounds(): Bounds {
        val a = imageDiagonal.a
        val b = imageDiagonal.b
        val d = a - b
        return BoundingBox(
                Math.min(a.x, b.x).toDouble(),
                Math.min(a.y, b.y).toDouble(),
                d.width.toDouble(),
                d.height.toDouble()
        )
    }
}