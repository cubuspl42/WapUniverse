package wapuniverse.model.impl

import javafx.collections.FXCollections.observableArrayList
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2d
import wapuniverse.model.WapObject
import wapuniverse.model.World
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.contains

class WorldImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : World {
    override val objects = observableArrayList<WapObjectImpl>()!!

    fun objectsAt(point: Vec2d): Set<WapObject> =
            objects.filter { it.boundingBox.value.contains(point) }.toSet()

    fun objectsIntersecting(bounds: Bounds): Set<WapObject> =
            objects.filter { it.boundingBox.value.intersects(bounds) }.toSet()

    fun deleteObjects(objectsToDelete: Set<WapObject>) {
        objects.removeAll(objectsToDelete)
    }

    fun init() {
        objects.add(WapObjectImpl(editorContext, rezIndex).apply {
            imageSet.set("LEVEL1_IMAGES_OFFICER")
        })
        objects.add(WapObjectImpl(editorContext, rezIndex).apply {
            imageSet.set("LEVEL1_IMAGES_SOLDIER")
            x.set(128)
        })
    }
}