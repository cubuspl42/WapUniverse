package wapuniverse.model

import javafx.beans.value.ObservableStringValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2i

interface Plane {
    val entities: ObservableList<out Entity>

    val tiles: ObservableMap<Vec2i, Int>

    val selectedObjects: ObservableSet<out Entity>

    val name: ObservableStringValue

    val imageSet: String
}