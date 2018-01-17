package wapuniverse.geom

import javafx.geometry.BoundingBox
import javafx.geometry.Rectangle2D

data class Rect2i(val minX: Int = 0, val minY: Int = 0, val width: Int = 0, val height: Int = 0) {
    companion object {
        fun fromBounds(minX: Int, minY: Int, maxX: Int, maxY: Int): Rect2i {
            assert(maxX >= minX)
            assert(maxY >= minY)
            return Rect2i(minX, minY, maxX - minX, maxY - minY)
        }
    }

    val maxX: Int
        get() = minX + width

    val maxY: Int
        get() = minY + height

    val minV: Vec2i
        get() = Vec2i(minX, minY)

    val maxV: Vec2i
        get() = Vec2i(maxX, maxY)

    val size: Vec2i
        get() = Vec2i(width, height)

    fun contains(x: Int, y: Int): Boolean {
        return x >= minX && y >= minY && x < maxX && y < maxY
    }

    fun contains(x: Double, y: Double): Boolean {
        return x >= minX && y >= minY && x < maxX && y < maxY
    }

    fun contains(v: Vec2d) = contains(v.x, v.y)

    fun intersects(rect: Rect2i): Boolean {
        return toRectangle2D().intersects(rect.toRectangle2D())
    }

    fun toRectangle2D(): Rectangle2D {
        return Rectangle2D(minX.toDouble(), minY.toDouble(), width.toDouble(), height.toDouble())
    }

    fun union(v: Vec2i): Rect2i {
        val minX = Math.min(v.x, minX)
        val maxX = Math.max(v.x, maxX)
        val minY = Math.min(v.y, minY)
        val maxY = Math.max(v.y, maxY)
        return fromBounds(minX, minY, maxX, maxY)
    }

    fun toBoundingBox() =
            BoundingBox(minX.toDouble(), minY.toDouble(), width.toDouble(), height.toDouble())

    fun scaled(a: Int) =
            Rect2i(minX * a, minY * a, width * a, height * a)

    fun scaledDown(a: Int) =
            Rect2i(minX / a, minY / a, width / a, height / a)
}
