package wapuniverse.geom

import javafx.geometry.Point2D

data class Vec2d(val x: Double = 0.0, val y: Double = 0.0) {
    companion object {
        val UNIT = Vec2d(1.0, 1.0)
    }

    val width: Double
        get() = Math.abs(x)

    val height: Double
        get() = Math.abs(y)

    infix operator fun plus(v: Vec2d): Vec2d {
        return Vec2d(x + v.x, y + v.y)
    }

    infix operator fun minus(v: Vec2d): Vec2d {
        return Vec2d(x - v.x, y - v.y)
    }

    infix operator fun div(a: Double): Vec2d {
        return Vec2d(x / a, y / a)
    }

    operator fun unaryMinus(): Vec2d {
        return Vec2d(-x, -y)
    }

    fun toPoint2D(): Point2D {
        return Point2D(x, y)
    }
}
