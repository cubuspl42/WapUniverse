package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import wapuniverse.model.AreaSelection
import wapuniverse.model.Entity
import wapuniverse.model.WapObject

class AreaSelectionImpl : AreaSelection {
    override val boundingBox = SimpleObjectProperty<BoundingBox>()
    override val preselectedObjects = SimpleObjectProperty<Set<Entity>>()
}
