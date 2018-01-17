package wapuniverse.view.ext

import javafx.geometry.BoundingBox
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D

fun Rectangle2D.toBounds() =
        BoundingBox(minX, minY, width, height)

fun Rectangle2D.topLeftCorner(): Point2D = Point2D(minX, minY)

fun Rectangle2D.bottomLeftCorner(): Point2D = Point2D(minX, maxY)

fun Rectangle2D.bottomRightCorner(): Point2D = Point2D(maxX, maxY)

fun Rectangle2D.topRightCorner(): Point2D = Point2D(maxX, minY)