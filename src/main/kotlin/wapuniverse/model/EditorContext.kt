package wapuniverse.model

import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.rez.RezIndex

interface EditorContext {
    val world: World
}

fun EditorContext(rezIndex: RezIndex): EditorContext = EditorContextImpl(rezIndex)
