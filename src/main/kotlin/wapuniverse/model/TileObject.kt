package wapuniverse.model

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableMap
import wapuniverse.geom.Vec2i

interface TileObject : Entity {
    val tilePosition: ObservableValue<Vec2i>
    val tiles: ObservableMap<Vec2i, Int>
}
