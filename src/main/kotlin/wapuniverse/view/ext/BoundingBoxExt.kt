package wapuniverse.view.ext

import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Rectangle2D
import wapuniverse.geom.Vec2d

fun BoundingBox.contains(point: Vec2d): Boolean =
        this.contains(point.x, point.y)

fun Bounds.toRectangle2D() =
        Rectangle2D(minX, minY, width, height)
