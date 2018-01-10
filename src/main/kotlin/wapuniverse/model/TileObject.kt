package wapuniverse.model

import javafx.collections.ObservableMap
import wapuniverse.geom.Vec2i

interface TileObject {
    val tiles: ObservableMap<Vec2i, Int>
}