package wapuniverse.model

import wapuniverse.geom.Vec2i
import wapuniverse.model.util.Disposable

class EditObjectContext(
        val wapObject: WapObject
) : Disposable() {
    fun setPosition(position: Vec2i) {
        wapObject.setPosition(position)
    }

    fun save() {
        dispose()
    }
}
