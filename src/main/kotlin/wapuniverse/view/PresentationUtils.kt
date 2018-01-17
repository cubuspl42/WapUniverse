package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Transform
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.view.ext.map
import wapuniverse.view.ext.toRectangle2D
import wapuniverse.view.ext.transform

@JvmName("transformedRectangle_fromBounds")
fun transformedRectangle(bounds: ObservableValue<BoundingBox>, transform: ObservableValue<Transform>): Rectangle {
    return transformedRectangle(bounds.map { it.toRectangle2D() }, transform)
}

fun transformedRectangle(rectangle: ObservableValue<Rectangle2D>, transform: ObservableValue<Transform>): Rectangle {
    val transformedBounds = combine(rectangle, transform) { rectangleVal, transformVal ->
        transformVal.transform(rectangleVal)
    }
    return Rectangle().apply {
        xProperty().bind(transformedBounds.map { it.minX.toInt().toDouble() + 0.5 })
        yProperty().bind(transformedBounds.map { it.minY.toInt().toDouble() + 0.5 })
        widthProperty().bind(transformedBounds.map { it.width })
        heightProperty().bind(transformedBounds.map { it.height })
    }
}

fun square(position: ObservableValue<Point2D>, width: Double) =
        Rectangle(width, width).apply {
            xProperty().bind(position.map { it.x })
            yProperty().bind(position.map { it.y })
        }

fun line(startPosition: ObservableValue<Point2D>, endPosition: ObservableValue<Point2D>) =
        Line().apply {
            startXProperty().bind(startPosition.map { it.x })
            startYProperty().bind(startPosition.map { it.y })
            endXProperty().bind(endPosition.map { it.x })
            endYProperty().bind(endPosition.map { it.y })
        }

fun transformedLine(
        startPosition: ObservableValue<Point2D>,
        endPosition: ObservableValue<Point2D>,
        transform: ObservableValue<Transform>
) =
        line(
                transform.transform(startPosition),
                transform.transform(endPosition)
        )

