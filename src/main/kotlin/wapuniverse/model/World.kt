package wapuniverse.model

import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2i

interface World {
    val entities: ObservableList<out Entity>

    val tiles: ObservableMap<Vec2i, Int>

    val selectedObjects: ObservableSet<out Entity>
}
