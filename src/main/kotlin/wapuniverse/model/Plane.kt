package wapuniverse.model

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.observableSet
import javafx.collections.FXCollections.unmodifiableObservableMap
import javafx.collections.FXCollections.unmodifiableObservableSet
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.UnmodifiableCollection
import wapuniverse.rez.RezImageMetadata
import wapuniverse.rez.RezIndex

class Plane(
        val world: World,
        wwdPlane: WwdPlane,
        private val rezIndex: RezIndex
) {
    val name = wwdPlane.name

    val imageSet = wwdPlane.imageSets.first()

    @UnmodifiableCollection
    val tiles: ObservableMap<Vec2i, Int>

    @UnmodifiableCollection
    val wapObjects: ObservableSet<WapObject>

    private val mTiles = observableHashMap<Vec2i, Int>()

    private val mWapObjects: ObservableSet<WapObject> = createWapObjectsSet(wwdPlane)

    init {
        tiles = unmodifiableObservableMap(mTiles)
        wapObjects = unmodifiableObservableSet(mWapObjects)

        for (i in 0 until wwdPlane.tilesHigh) {
            for (j in 0 until wwdPlane.tilesWide) {
                mTiles[Vec2i(j, i)] = wwdPlane.getTile(i, j)
            }
        }
    }

    fun findTileImageMetadata(tileId: Int): RezImageMetadata? {
        if (tileId < 0) return null
        val prefix = world.imageDir.replace('\\', '_').removePrefix("_")
        val imageSetId = "${prefix}_$imageSet"
        return rezIndex.findImageMetadata(imageSetId, tileId)
    }

    private fun createWapObjectsSet(wwdPlane: WwdPlane) =
            observableSet<WapObject>(wwdPlane.objects.map { WapObject(this, it) }.toSet())!!
}
