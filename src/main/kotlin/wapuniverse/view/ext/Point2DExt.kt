package wapuniverse.view.ext

import javafx.geometry.Point2D
import wapuniverse.geom.Vec2d

fun Point2D.toVec2d(): Vec2d {
    return Vec2d(x, y)
}
