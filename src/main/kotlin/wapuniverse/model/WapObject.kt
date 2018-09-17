package wapuniverse.model

import io.github.jwap32.v1.WwdObject
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2i

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

    private fun calculateImageDiagonal(): EucVec2i {
        val md = imageMetadata ?: return EucVec2i(position, position)
        val s = Vec2i(if (mirrored) -1 else 1, if (inverted) -1 else 1)
        val ev = (EucVec2i(Vec2i(), md.size) - md.size / 2 + md.offset).scaled(s)
        return ev + position
    }
}