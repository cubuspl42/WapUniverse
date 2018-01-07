package wapuniverse.view.ext

import javafx.scene.Node
import wapuniverse.geom.Vec2d

fun Node.parentToLocal(v: Vec2d): Vec2d =
        parentToLocal(v.toPoint2D()).toVec2d()
