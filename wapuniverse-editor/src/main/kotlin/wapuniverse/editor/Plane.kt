package wapuniverse.editor

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i

class Plane(
        val world: World,
        val name: String
) {
    private val objectsMut = observableArrayList<WapObject>()

    val objects = unmodifiableObservableList(objectsMut)!!

    init {
        objectsMut.addAll(
                WapObject(this, Vec2i(0, 0), "LEVEL1_IMAGES_OFFICER"),
                WapObject(this, Vec2i(128, 128), "LEVEL1_IMAGES_SOLDIER"),
                WapObject(this, Vec2i(256, 256), "LEVEL1_IMAGES_SOLDIER")
        )
    }

    internal fun findObjects(area: Rect2i): Set<WapObject> {
        val boundingBoxes = objects.map { it.boundingBox }

        return objects.filter { area.collides(it.boundingBox) }.toSet()
    }
}
