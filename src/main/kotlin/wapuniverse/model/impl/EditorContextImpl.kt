package wapuniverse.model.impl

import wapuniverse.model.EditorContext
import wapuniverse.rez.RezIndex

class EditorContextImpl(
        rezIndex: RezIndex
) : EditorContext {
    override val world = WorldImpl(rezIndex)
}
