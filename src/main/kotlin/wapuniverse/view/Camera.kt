package wapuniverse.view

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.transform.Affine
import javafx.scene.transform.Transform

class Camera {
    val transform = SimpleObjectProperty<Transform>(Affine())
}
