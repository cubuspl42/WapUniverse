package wapuniverse.view.extensions

import javafx.scene.input.MouseEvent
import wapuniverse.geom.Vec2d

val MouseEvent.point: Vec2d
    get() = Vec2d(x, y)
