package wapuniverse.view.ext

import javafx.scene.input.MouseEvent
import wapuniverse.geom.Vec2d

val MouseEvent.position: Vec2d
    get() = Vec2d(x, y)
