package wapuniverse.geom

/**
 * Euclidean vector
 */
data class EucVec2i(val a: Vec2i, val b: Vec2i) {
    infix operator fun minus(v: Vec2i): EucVec2i {
        return EucVec2i(a - v, b - v)
    }

    infix operator fun plus(v: Vec2i): EucVec2i {
        return EucVec2i(a + v, b + v)
    }

    fun delta() = b - a

    fun scaled(v: Vec2i) = EucVec2i(a.scaled(v), b.scaled(v))
}
