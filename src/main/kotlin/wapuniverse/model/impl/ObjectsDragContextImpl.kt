package wapuniverse.model.impl

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import wapuniverse.geom.Vec2d
import wapuniverse.model.ObjectsDrag
import wapuniverse.model.ObjectsDragContext
import wapuniverse.view.ext.map

class ObjectsDragContextImpl(
        private val origin: Vec2d,
        private val objectsDragProperty: Property<ObjectsDrag>,
        private val world: WorldImpl
) : ObjectsDragContext {
    private val destination = SimpleObjectProperty<Vec2d>(origin)

    private val delta = destination.map { it - origin }

    private val snapshot = world.entities.map { Pair(it, it.position.value) }.toMap()

    init {
        objectsDragProperty.value = ObjectsDragImpl(origin, delta)
    }

    override fun setDestination(dest: Vec2d) {
        destination.value = dest
        snapshot.entries.forEach { (wapObject, originalPosition) ->
            wapObject.setPosition(originalPosition + delta.value.toVec2i())
        }
    }

    override fun commit() {
        objectsDragProperty.value = null
    }
}