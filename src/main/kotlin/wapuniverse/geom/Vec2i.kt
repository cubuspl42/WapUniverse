package wapuniverse.geom

data class Vec2i(val x: Int = 0, val y: Int = 0) {
    companion object {
        val UNIT = Vec2i(1, 1)
    }

    fun toVec2d(): Vec2d {
        return Vec2d(x.toDouble(), y.toDouble())
    }

    infix operator fun minus(v: Vec2i): Vec2i {
        return Vec2i(x - v.x, y - v.y)
    }

    infix operator fun plus(v: Vec2i): Vec2i {
        return Vec2i(x + v.x, y + v.y)
    }

    val width: Int
        get() = Math.abs(x)

    val height: Int
        get() = Math.abs(y)
}
