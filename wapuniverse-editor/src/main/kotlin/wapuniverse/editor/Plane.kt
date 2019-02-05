package wapuniverse.editor

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import wapuniverse.editor.util.observableIntMatrix
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i

val emptyTile = -1

class Plane(
        val world: World,
        wwdPlane: WwdPlane
) {
    val name = wwdPlane.name

    val tiles = observableIntMatrix(wwdPlane.tilesHigh, wwdPlane.tilesWide) { emptyTile }

    val fqTilesetId: String = "LEVEL1_TILES_ACTION"

    private val objectsMut = observableArrayList<WapObject>()

    val objects = unmodifiableObservableList(objectsMut)!!

    init {
        wwdPlane.objects.forEach { wwdObject ->
            objectsMut.add(WapObject(
                    this,
                    Vec2i(wwdObject.x, wwdObject.y),
                    wwdObject.imageSet
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
}
