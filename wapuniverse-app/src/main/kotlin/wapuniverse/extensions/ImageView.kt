package wapuniverse.extensions

import javafx.scene.image.ImageView
import org.reactfx.value.Val
import wapuniverse.geom.Vec2i

fun ImageView.bindPosition(position: Val<Vec2i>) {
    xProperty().bind(position.map { it.x })
    yProperty().bind(position.map { it.y })
}