package wapuniverse.model

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

    val width: Int
        get() = Math.abs(x)

    val height: Int
        get() = Math.abs(y)
}
