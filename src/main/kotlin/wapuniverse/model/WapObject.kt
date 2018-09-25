package wapuniverse.model

import io.github.jwap32.v1.WwdObject
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2i
import wapuniverse.rez.RezImageMetadata
import wapuniverse.util.booleanProperty
import wapuniverse.util.combine
import wapuniverse.util.objectProperty
import wapuniverse.view.extensions.map

data class WapObjectData(
        val id: Int,
        val name: String,
        val logic: String,
        val imageSet: String,
        val animation: String,
        val x: Int,
        val y: Int,
        val z: Int,
        val i: Int,
        val mirrored: Boolean,
        val inverted: Boolean
)

class WapObject(
        val plane: Plane,
        wapObjectData: WapObjectData
) {
    private val world = plane.world

    val data: ObservableValue<WapObjectData>

    val position: ObservableValue<Vec2i>

    val i: ObservableValue<Int>

    val imageSet: ObservableValue<String>

    val mirrored: ObservableValue<Boolean>

    val inverted: ObservableValue<Boolean>

    val imageMetadata: ObservableValue<RezImageMetadata?>

    val imageDiagonal: ObservableValue<EucVec2i?>

    val bounds: ObservableValue<Bounds?>

    val isSelected: ObservableBooleanValue

    internal val iIsSelected = booleanProperty(false)

    private val mData = objectProperty(wapObjectData)

    init {
        data = mData
        i = data.map { it.i }
        imageSet = data.map { it.imageSet }
        mirrored = data.map { it.mirrored }
        inverted = data.map { it.inverted }
        imageMetadata = combine(imageSet, i) { imageSet, i ->
            world.findObjectImageMetadata(imageSet, i)
        }
        position = data.map { Vec2i(it.x, it.y) }
        isSelected = iIsSelected
        imageDiagonal = imageMetadata.map { calculateImageDiagonal(it) }
        bounds = imageDiagonal.map { calculateBounds(it) }
    }

    internal fun setPosition(pos: Vec2i) {
        mData.value = mData.value.copy(x = pos.x, y = pos.y)
    }

    private fun calculateImageDiagonal(imageMetadata: RezImageMetadata?): EucVec2i {
        val p = position.value
        val md = imageMetadata ?: return EucVec2i(p, p)
        val ev = (EucVec2i(Vec2i(), md.size) - md.size / 2 + md.offset)
        return ev + p
    }

    private fun calculateBounds(imageDiagonal: EucVec2i): Bounds {
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

fun wapObject(plane: Plane, wwdObject: WwdObject) =
        WapObject(plane, WapObjectData(
                id = wwdObject.id,
                name = wwdObject.name,
                logic = wwdObject.logic,
                imageSet = wwdObject.imageSet,
                animation = wwdObject.animation,
                x = wwdObject.x,
                y = wwdObject.y,
                z = wwdObject.z,
                i = wwdObject.i,
                mirrored = wwdObject.drawFlags.mirror,
                inverted = wwdObject.drawFlags.invert
        ))
