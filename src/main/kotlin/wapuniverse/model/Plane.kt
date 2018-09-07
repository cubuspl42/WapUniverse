package wapuniverse.model

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.unmodifiableObservableMap
import javafx.collections.ObservableMap
import wapuniverse.geom.Vec2i
import wapuniverse.model.util.UnmodifiableCollection

class Plane(
        val world: World,
        wwdPlane: WwdPlane
) {
    val name = wwdPlane.name

    val imageSet = wwdPlane.imageSets.first()

    @UnmodifiableCollection
    val tiles: ObservableMap<Vec2i, Int>

    private val mTiles = observableHashMap<Vec2i, Int>()

    init {
        tiles = unmodifiableObservableMap(mTiles)

        for (i in 0 until wwdPlane.tilesHigh) {
            for (j in 0 until wwdPlane.tilesWide) {
                mTiles[Vec2i(j, i)] = wwdPlane.getTile(i, j)
            }
        }
    }
}
