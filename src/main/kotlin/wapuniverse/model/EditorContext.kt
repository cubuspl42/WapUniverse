package wapuniverse.model

import wapuniverse.model.impl.EditorContextImpl

interface EditorContext {
    val world: World
}

fun EditorContext(): EditorContext = EditorContextImpl()
