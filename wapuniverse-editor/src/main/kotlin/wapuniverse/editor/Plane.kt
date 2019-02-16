package wapuniverse.editor

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import org.reactfx.value.Var
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.editor.util.Matrix
import wapuniverse.editor.util.observableIntMatrix
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i

val emptyTile = -1

class Plane(
        val world: World,
        tileSetMetadataSupplier: TileSetMetadataSupplier,
        private val wwdPlane: WwdPlane
) {
    val name = newSimpleVar(wwdPlane.name)

    val movement = newSimpleVar(Vec2i(100, 100))

    val fillColor = newSimpleVar(0)

    val z = newSimpleVar(0)

    // TODO: image sets

    val tiles = observableIntMatrix(wwdPlane.tilesHigh, wwdPlane.tilesWide) { emptyTile }

    private val imageSet = wwdPlane.imageSets.first()

    val fqImageSetId: String = makeFqImageSetId(world.imageDir, imageSet) // TODO: Val

    val tileSet = tileSetMetadataSupplier.listTiles(fqImageSetId)

    private val objectsMut = observableArrayList<WapObject>()

    val objects = unmodifiableObservableList(objectsMut)!!

    private var selectedObjectsVar = setOf<WapObject>()

    val selectedObjects: Set<WapObject>
        get() = selectedObjectsVar

    init {
        wwdPlane.objects.forEach { wwdObject ->
            objectsMut.add(WapObject(
                    this,
                    listOf(
                            WapObjectIntAttrKey.X to wwdObject.x,
                            WapObjectIntAttrKey.Y to wwdObject.y,
                            WapObjectIntAttrKey.I to wwdObject.i
                    ), listOf(WapObjectStringAttrKey.IMAGE_SET to wwdObject.imageSet)
            ))
        }

        tiles.forEach { i, j, _ ->
            tiles.put(i, j, wwdPlane.getTile(i, j))
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

    internal fun insertObject(position: Vec2i) {
        objectsMut.add(WapObject(this, listOf(
                WapObjectIntAttrKey.X to position.x,
                WapObjectIntAttrKey.Y to position.y,
                WapObjectIntAttrKey.I to -1
        ), listOf(
                WapObjectStringAttrKey.IMAGE_SET to "GAME_TREASURE_COINS"
        )))
    }

    internal fun removeSelectedObjects() {
//        objectsMut.removeAll(selectedObjects)
        selectedObjects.forEach { objectsMut.remove(it) }
        unselectAllObjects()
    }

    internal fun putTile(offset: Vec2i, tileId: Int) {
        tiles.put(offset.y, offset.x, tileId)
    }

    fun toWwdPlane(): WwdPlane {
        return wwdPlane.copy( // TODO
                tilesWide = tiles.columnCount, tilesHigh = tiles.rowCount,
                tiles = tiles.toIntArray(),
                objects = objects.map { it.toWwdObject() }.toMutableList()
        )
    }
}

private fun Matrix<Int>.toIntArray(): IntArray {
    val array = IntArray(rowCount * columnCount)
    forEach { i, j, value ->
        array[i * columnCount + j] = value
    }
    return array
}

private fun makeFqImageSetId(imageDir: String, imageSet: String): String {
    val normalizedImageDir = imageDir.replace('\\', '_').removePrefix("_")
    return "${normalizedImageDir}_$imageSet"
}
