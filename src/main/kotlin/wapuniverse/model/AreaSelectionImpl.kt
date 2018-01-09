package wapuniverse.model

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2d

class AreaSelectionImpl : AreaSelection {
    override val boundingBox = SimpleObjectProperty<BoundingBox>()
    override val preselectedObjects = SimpleObjectProperty<Set<WapObject>>()
}
