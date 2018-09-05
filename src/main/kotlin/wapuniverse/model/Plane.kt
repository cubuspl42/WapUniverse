package wapuniverse.model

import io.github.jwap32.v1.WwdPlane
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.unmodifiableObservableMap
import javafx.collections.ObservableMap
import wapuniverse.model.util.UnmodifiableCollection

class Plane(wwdPlane: WwdPlane) {
    val name = wwdPlane.name

    @UnmodifiableCollection
    val tiles: ObservableMap<Vec2i, Int>

    private val mTiles = observableHashMap<Vec2i, Int>()

    init {
        tiles = unmodifiableObservableMap(mTiles)

        for (i in 0 until wwdPlane.tilesHigh) {
            for (j in 0 until wwdPlane.tilesWide) {
                mTiles[Vec2i(i, j)] = wwdPlane.getTile(i, j)
            }
        }
    }
}
