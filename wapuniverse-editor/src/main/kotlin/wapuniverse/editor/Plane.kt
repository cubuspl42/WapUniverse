package wapuniverse.editor

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import wapuniverse.editor.util.observableIntMatrix
import wapuniverse.geom.Rect2i

val emptyTile = -1

class Plane(
        val world: World,
        wwdPlane: WwdPlane
) {
    val name = wwdPlane.name

    val tiles = observableIntMatrix(wwdPlane.tilesHigh, wwdPlane.tilesWide) { emptyTile }

    private val imageSet = wwdPlane.imageSets.first()

    val fqImageSetId: String = makeFqImageSetId(world.imageDir, imageSet)

    private val objectsMut = observableArrayList<WapObject>()

    val objects = unmodifiableObservableList(objectsMut)!!

    private var selectedObjectsVar = setOf<WapObject>()

    val selectedObjects: Set<WapObject>
        get() = selectedObjectsVar

    init {
        wwdPlane.objects.forEach { wwdObject ->
            objectsMut.add(WapObject(
                    this,
                    wwdObject
            ))
        }

        tiles.forEach { i, j, _ ->
            tiles.set(i, j, wwdPlane.getTile(i, j))
        }
    }

    fun getTileMetadata(tileId: Int): ImageMetadata? {
        return world.supplyMetadata("LEVEL1_TILES", tileId)
    }

    internal fun findObjects(area: Rect2i): Set<WapObject> {
        return objects.filter { it.boundingBox.value?.collides(area) == true }.toSet()
    }

    internal fun selectObjects(objects: Set<WapObject>) {
        selectedObjectsVar = objects
        objects.forEach { it.select() }
    }

    fun unselectAllObjects() {
        selectedObjectsVar.forEach { it.unselect() }
        selectedObjectsVar = emptySet()
    }
}

private fun makeFqImageSetId(imageDir: String, imageSet: String): String {
    val normalizedImageDir = imageDir.replace('\\', '_').removePrefix("_")
    return "${normalizedImageDir}_$imageSet"
}
