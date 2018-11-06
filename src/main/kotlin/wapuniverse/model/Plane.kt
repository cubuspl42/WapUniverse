package wapuniverse.model

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.observableSet
import javafx.collections.FXCollections.unmodifiableObservableMap
import javafx.collections.FXCollections.unmodifiableObservableSet
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.ChunkMap
import wapuniverse.model.util.UnmodifiableCollection
import wapuniverse.rez.RezImageMetadata
import wapuniverse.rez.RezIndex

class Plane(
        val world: World,
        private val wwdPlane: WwdPlane,
        private val rezIndex: RezIndex
) {
    val name = wwdPlane.name

    val imageSet = wwdPlane.imageSets.first()

    val tilesetImageSet = makeTilesetImageSet()

    val tileset = makeTileset()

    val tiles = ChunkMap()

    @UnmodifiableCollection
    val wapObjects: ObservableSet<WapObject>

    private val size = Vec2i(wwdPlane.tilesWide, wwdPlane.tilesHigh)

    private val mWapObjects: ObservableSet<WapObject> = createWapObjectsSet(wwdPlane)

    init {
        wapObjects = unmodifiableObservableSet(mWapObjects)

        for (i in 0 until wwdPlane.tilesHigh) {
            for (j in 0 until wwdPlane.tilesWide) {
                tiles.setTile(Vec2i(j, i), wwdPlane.getTile(i, j))
            }
        }
    }

    fun findTileImageMetadata(tileId: Int): RezImageMetadata? {
        if (tileId < 0) return null
        val prefix = world.imageDir.replace('\\', '_').removePrefix("_")
        val imageSetId = "${prefix}_$imageSet"
        return rezIndex.findImageMetadata(imageSetId, tileId)
    }

    internal fun findObjectsAt(point: Vec2i): List<WapObject> {
        return mWapObjects.filter { it.bounds.value?.contains(point.toPoint2D()) ?: false }
    }

    internal fun setTile(tileOffset: Vec2i, tileId: Int) {
        if (tileOffset.x in (0 until size.width) && tileOffset.y in (0 until size.height)) {
            tiles.setTile(tileOffset, tileId)
        }
    }

    fun getTile(tileOffset: Vec2i) = tiles.getTile(tileOffset)

    private fun makeTilesetImageSet(): String {
        val prefix = world.imageDir.replace('\\', '_').removePrefix("_")
        return "${prefix}_$imageSet"
    }

    private fun makeTileset(): List<Int>? {
        val imageSet = rezIndex.imageSets[tilesetImageSet] ?: return null
        return imageSet.frames.keys.filter { it >= 0 }
    }

    private fun createWapObjectsSet(wwdPlane: WwdPlane) =
            observableSet<WapObject>(wwdPlane.objects.map { wapObject(this, it) }.toSet())!!

    fun toWwdPlane(): WwdPlane {
        val wwdPlane = wwdPlane.clone()
        wwdPlane.objects = wapObjects.map { it.toWwdObject() }.toMutableList()
        for (i in 0 until wwdPlane.tilesHigh) {
            for (j in 0 until wwdPlane.tilesWide) {
                wwdPlane.setTile(i, j, tiles.getTile(Vec2i(j, i)))
            }
        }
        return wwdPlane
    }
}
