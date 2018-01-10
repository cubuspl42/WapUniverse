package wapuniverse.model

import wapuniverse.geom.Vec2d

interface ObjectsDragContext {
    fun setDestination(dest: Vec2d)
    fun commit()
}
