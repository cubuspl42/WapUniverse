package wapuniverse.model

import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import wapuniverse.geom.Vec2i

interface World {
    val planes: ObservableList<out Plane>
}
