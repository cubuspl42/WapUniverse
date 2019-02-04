package wapuniverse.geom

import javafx.geometry.Point2D
import kotlin.math.absoluteValue

data class Vec2i(val x: Int = 0, val y: Int = 0) {
    companion object {
        val UNIT = Vec2i(1, 1)
    }

    infix operator fun minus(v: Vec2i): Vec2i {
        return Vec2i(x - v.x, y - v.y)
    }

    infix operator fun plus(v: Vec2i): Vec2i {
        return Vec2i(x + v.x, y + v.y)
    }

    infix operator fun times(a: Int): Vec2i {
        return Vec2i(x * a, y * a)
    }

    operator fun div(a: Int): Vec2i {
        return Vec2i(x / a, y / a)
    }

    fun scaled(v: Vec2i) = Vec2i(x * v.x, y * v.y)

    val width: Int
        get() = Math.abs(x)

    val height: Int
        get() = Math.abs(y)

    val size: Size2i
        get() = Size2i(x.absoluteValue, y.absoluteValue)

    fun toVec2d() = Vec2d(x.toDouble(), y.toDouble())

    fun toPoint2D(): Point2D {
        return Point2D(x.toDouble(), y.toDouble())
    }

    operator fun plus(size: Size2i): Vec2i {
        return this + size.toVec2i()
    }
}
