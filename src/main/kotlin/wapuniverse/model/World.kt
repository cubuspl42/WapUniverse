package wapuniverse.model

import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import wapuniverse.geom.Vec2i

interface World {
    val objects: ObservableList<out WapObject>

    val tiles: ObservableMap<Vec2i, Int>
}
