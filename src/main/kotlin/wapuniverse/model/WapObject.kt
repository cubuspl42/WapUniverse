package wapuniverse.model

import io.github.jwap32.v1.WwdObject
import javafx.beans.value.ObservableBooleanValue
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2i
import wapuniverse.util.booleanProperty

class WapObject(
        val plane: Plane,
        wwdObject: WwdObject
) {
    private val world = plane.world

    val position = Vec2i(wwdObject.x, wwdObject.y)

    val i = wwdObject.i

    val imageSet = wwdObject.imageSet

    val mirrored = wwdObject.drawFlags.mirror

    val inverted = wwdObject.drawFlags.invert

    val imageMetadata = world.findObjectImageMetadata(imageSet, i)

    val imageDiagonal = calculateImageDiagonal()

    val bounds: Bounds = createBounds()

    val isSelected: ObservableBooleanValue

    internal val iIsSelected = booleanProperty(false)

    init {
        isSelected = iIsSelected
    }

    private fun calculateImageDiagonal(): EucVec2i {
        val md = imageMetadata ?: return EucVec2i(position, position)
        val ev = (EucVec2i(Vec2i(), md.size) - md.size / 2 + md.offset)
        return ev + position
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