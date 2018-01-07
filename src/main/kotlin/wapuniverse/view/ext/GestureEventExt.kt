package wapuniverse.view.ext

import javafx.scene.input.GestureEvent
import wapuniverse.geom.Vec2d

val GestureEvent.position: Vec2d
    get() = Vec2d(x, y)
