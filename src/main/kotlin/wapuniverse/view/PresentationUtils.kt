package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Transform
import org.fxmisc.easybind.EasyBind.combine

fun presentRectangle(bounds: ObservableValue<BoundingBox>, transform: ObservableValue<Transform>): Rectangle {
    val transformedBounds = combine(bounds, transform) { boundsVal, transformVal ->
        transformVal.transform(boundsVal)
    }
    return Rectangle().apply {
        xProperty().bind(transformedBounds.map { it.minX.toInt().toDouble() + 0.5 })
        yProperty().bind(transformedBounds.map { it.minY.toInt().toDouble() + 0.5 })
        widthProperty().bind(transformedBounds.map { it.width })
        heightProperty().bind(transformedBounds.map { it.height })
    }
}
