package wapuniverse.model.impl

import javafx.collections.FXCollections.observableSet
import wapuniverse.model.WapObject
import wapuniverse.model.World

class WorldImpl : World {
    override val objects = observableSet<WapObject>(
            WapObjectImpl().apply {
                imageSet.set("LEVEL1_IMAGES_OFFICER")
            },
            WapObjectImpl().apply {
                imageSet.set("LEVEL1_IMAGES_SOLDIER")
                x.set(128)
            }
    )!!
}
