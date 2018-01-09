package wapuniverse.model

import wapuniverse.geom.Vec2d

interface AreaSelectionContext {
    fun setEndPoint(endPoint: Vec2d)
    fun commit()
}