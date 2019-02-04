package wapuniverse.geom

data class Size2i(val width: Int = 0, val height: Int = 0) {
    init {
        check(width >= 0 && height >= 0)
    }

    operator fun div(a: Int): Size2i {
        check(a > 0)
        return Size2i(width / a, height / a)
    }

    fun toVec2i() = Vec2i(width, height)
}
