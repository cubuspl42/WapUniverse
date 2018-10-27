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
        val score: Int,
        val points: Int,
        val smarts: Int,
        val powerup: Int,
        val damage: Int,
        val health: Int,
        val speedX: Int,
        val speedY: Int,
        val faceDir: Int,
        val xMin: Int,
        val xMax: Int,
        val direction: Int,
        val yMin: Int,
        val yMax: Int,
        val speed: Int,
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

    fun toWwdObject(): WwdObject {
        val data = mData.value
        return WwdObject(
                id = data.id,
                name = data.name,
                logic = data.logic,
                imageSet = data.imageSet,
                animation = data.animation,
                x = data.x,
                y = data.y,
                z = data.z,
                i = data.i,
                score = data.score,
                points = data.points,
                smarts = data.smarts,
                powerup = data.powerup,
                damage = data.damage,
                health = data.health,
                speedX = data.speedX,
                speedY = data.speedY,
                faceDir = data.faceDir,
                xMin = data.xMin,
                xMax = data.xMax,
                direction = data.direction,
                yMin = data.yMin,
                yMax = data.yMax,
                speed = data.speed
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
                score = wwdObject.score,
                points = wwdObject.points,
                smarts = wwdObject.smarts,
                powerup = wwdObject.powerup,
                damage = wwdObject.damage,
                health = wwdObject.health,
                speedX = wwdObject.speedX,
                speedY = wwdObject.speedY,
                faceDir = wwdObject.faceDir,
                xMin = wwdObject.xMin,
                xMax = wwdObject.xMax,
                direction = wwdObject.direction,
                yMin = wwdObject.yMin,
                yMax = wwdObject.yMax,
                speed = wwdObject.speed,
                mirrored = wwdObject.drawFlags.mirror,
                inverted = wwdObject.drawFlags.invert
        ))
