package wapuniverse.editor

sealed class WapObjectAttrKey<T> {
    companion object {
        private val allKeysMut = mutableListOf<WapObjectAttrKey<*>>()

        val allKeys: List<WapObjectAttrKey<*>>
            get() = allKeysMut
    }
    init {
        allKeysMut.add(this)
    }
}

sealed class WapObjectIntAttrKey : WapObjectAttrKey<Int>() {
    object x : WapObjectIntAttrKey()

    object y : WapObjectIntAttrKey()

    object z : WapObjectIntAttrKey()

    object i : WapObjectIntAttrKey()

    object score : WapObjectIntAttrKey()

    object points : WapObjectIntAttrKey()

    object smarts : WapObjectIntAttrKey()

    object powerup : WapObjectIntAttrKey()

    object damage : WapObjectIntAttrKey()

    object health : WapObjectIntAttrKey()

    object speedX : WapObjectIntAttrKey()

    object speedY : WapObjectIntAttrKey()

    object faceDir : WapObjectIntAttrKey()

    object xMin : WapObjectIntAttrKey()

    object xMax : WapObjectIntAttrKey()

    object direction : WapObjectIntAttrKey()

    object yMin : WapObjectIntAttrKey()

    object yMax : WapObjectIntAttrKey()

    object speed : WapObjectIntAttrKey()
}

sealed class WapObjectStringAttrKey : WapObjectAttrKey<String>() {
    object id : WapObjectStringAttrKey()

    object name : WapObjectStringAttrKey()

    object logic : WapObjectStringAttrKey()

    object imageSet : WapObjectStringAttrKey()

    object animation : WapObjectStringAttrKey()
}
