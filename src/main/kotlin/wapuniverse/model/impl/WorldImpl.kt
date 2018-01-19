package wapuniverse.model.impl

import javafx.collections.FXCollections.observableArrayList
import wapuniverse.model.World
import wapuniverse.rez.RezIndex

class WorldImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : World {
    override val planes = observableArrayList<PlaneImpl>()!!

    override var imageSets = listOf<String>()

    override var prefixes = listOf<String>()

    override fun resolveImageSetId(shortId: String): String {
        val (imageSetPath, prefix) = imageSets.zip(prefixes).firstOrNull { (_, prefix) ->
            shortId.startsWith(prefix)
        } ?: return ""
        return shortId.replace(prefix, imageSetPath.replace('\\', '_'))
    }

    fun addPlane() =
            PlaneImpl(editorContext, rezIndex).also { planes.add(it) }
}
