package wapuniverse.model.impl

import wapuniverse.model.EditorContext

class EditorContextImpl : EditorContext {
    override val world = WorldImpl()
}
