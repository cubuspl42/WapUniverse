package wapuniverse.view.ext

import javafx.geometry.Rectangle2D
import javafx.scene.transform.Transform

fun Transform.transform(rectangle: Rectangle2D) =
        transform(rectangle.toBounds()).toRectangle2D()
