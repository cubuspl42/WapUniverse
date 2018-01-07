package wapuniverse.model.impl

import javafx.collections.FXCollections.observableSet
import wapuniverse.model.WapObject
import wapuniverse.model.World
import wapuniverse.rez.RezIndex

class WorldImpl(
        rezIndex: RezIndex
) : World {
    override val objects = observableSet<WapObject>(
            WapObjectImpl(rezIndex).apply {
                imageSet.set("LEVEL1_IMAGES_OFFICER")
            },
            WapObjectImpl(rezIndex).apply {
                imageSet.set("LEVEL1_IMAGES_SOLDIER")
                x.set(128)
            }
    )!!
}
