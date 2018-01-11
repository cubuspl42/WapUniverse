package wapuniverse.model

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox

interface AreaSelection {
    val boundingBox: ObservableValue<BoundingBox>
    val preselectedObjects: ObservableValue<out Set<Entity>>
}