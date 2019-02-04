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

    val minX: Int
        get() = position.x

    val maxX: Int
        get() = position.x + size.width

    val minY: Int
        get() = position.y

    val maxY: Int
        get() = position.y + size.width

    val xRange: IntRange
        get() = minX until maxX

    val yRange: IntRange
        get() = minY until maxY

    operator fun plus(v: Vec2i): Rect2i {
        return Rect2i(position + v, size)
    }

    fun collides(rect: Rect2i): Boolean {
        return xRange.intersects(rect.xRange) && yRange.intersects(rect.yRange)
    }
}

private fun IntRange.intersects(other: IntRange): Boolean {
    return !(other.endInclusive < start || other.start > endInclusive)
}
