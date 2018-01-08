package wapuniverse.view.ext

import javafx.geometry.Point2D
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i

fun Point2D.toVec2d(): Vec2d =
        Vec2d(x, y)

fun Point2D.toVec2i(): Vec2i =
        Vec2i(this.x.toInt(), this.y.toInt())
