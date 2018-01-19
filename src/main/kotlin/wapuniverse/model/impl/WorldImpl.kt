package wapuniverse.model.impl

import javafx.collections.FXCollections.observableArrayList
import wapuniverse.model.World
import wapuniverse.rez.RezIndex

class WorldImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : World {
    override val planes = observableArrayList<PlaneImpl>()!!

    fun addPlane() =
            PlaneImpl(editorContext, rezIndex).also { planes.add(it) }
}
