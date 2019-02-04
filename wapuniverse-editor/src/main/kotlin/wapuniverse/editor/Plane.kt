package wapuniverse.editor

import javafx.collections.FXCollections.*
import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2i

class Plane(val name: String) {
    private val objectsMut = observableArrayList<WapObject>()

    val objects = unmodifiableObservableList(objectsMut)!!

    init {
        objectsMut.addAll(
                WapObject(Vec2i(0, 0), "LEVEL1_IMAGES_OFFICER"),
                WapObject(Vec2i(128, 128), "LEVEL1_IMAGES_SOLDIER"),
                WapObject(Vec2i(256, 256), "LEVEL1_IMAGES_SOLDIER")
        )
    }

    internal fun findObjects(area: BoundingBox): Set<WapObject> {
        return objects.filter { area.intersects(it.boundingBox)}.toSet()
    }
}
