package wapuniverse.editor

interface WapObjectAttrKey<T> {
    companion object {
        val allKeys = (
                WapObjectIntAttrKey.values().map { it as WapObjectAttrKey<*> } +
                        WapObjectStringAttrKey.values().map { it as WapObjectAttrKey<*> }
                ).toList()
    }
}

enum class WapObjectIntAttrKey : WapObjectAttrKey<Int> {
    X,
    Y,
    Z,
    I,
    SCORE,
    POINTS,
    SMARTS,
    POWERUP,
    DAMAGE,
    HEALTH,
    SPEED_X,
    SPEED_Y,
    FACEDIR,
    X_MIN,
    X_MAX,
    DIRECTION,
    Y_MIN,
    Y_MAX,
    SPEED,
}

enum class WapObjectStringAttrKey : WapObjectAttrKey<String> {
    ID,
    NAME,
    LOGIC,
    IMAGE_SET,
    ANIMATION,
}


