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
                WapObject(this, Vec2i(0, 0), "LEVEL_OFFICER"),
                WapObject(this, Vec2i(128, 128), "LEVEL_SOLDIER"),
                WapObject(this, Vec2i(256, 256), "LEVEL_SOLDIER")
        )
    }

    internal fun findObjects(area: Rect2i): Set<WapObject> {
        return objects.filter { it.boundingBox.value.collides(area)}.toSet()
    }
}
