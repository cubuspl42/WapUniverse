package wapuniverse.view.ext

import javafx.geometry.BoundingBox
import wapuniverse.geom.Vec2d

fun BoundingBox.contains(point: Vec2d): Boolean =
        this.contains(point.x, point.y)
