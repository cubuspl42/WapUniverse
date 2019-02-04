package wapuniverse.geom

import kotlin.math.min

data class Rect2i(val position: Vec2i, val size: Size2i) {
    companion object {
        fun fromDiagonal(a: Vec2i, b: Vec2i): Rect2i {
            val d = (b - a)
            return Rect2i(Vec2i(min(a.x, b.x), min(a.y, b.y)), d.size)
        }

        fun fromCenter(center: Vec2i, size: Size2i) = Rect2i(center - (size.toVec2i() / 2), size)
    }

    fun intersects(boundingBox: Rect2i): Boolean {
        TODO()
    }

    operator fun plus(v: Vec2i): Rect2i {
        return Rect2i(position + v, size)
    }
}
