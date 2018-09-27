package wapuniverse.model

import wapuniverse.geom.Vec2i

class EditObjectContext(
        val wapObject: WapObject
) {
    fun setPosition(position: Vec2i) {
        wapObject.setPosition(position)
    }
}
