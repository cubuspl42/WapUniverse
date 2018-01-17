package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.model.EditorContext
import wapuniverse.model.Entity
import wapuniverse.model.WapObject
import wapuniverse.model.impl.resolveShortId
import wapuniverse.model.moveToolContext
import wapuniverse.model.selectToolContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.attachController
import wapuniverse.view.ext.map
import wapuniverse.view.util.observableValue

class WapObjectPresenter(
        private val rezImageProvider: RezImageProvider,
        private val camera: Camera,
        editorContext: EditorContext
) {
    private val selectToolContext = editorContext.selectToolContext

    private val moveToolContext = editorContext.moveToolContext

    fun presentObjectImageView(wapObject: WapObject): Node {
        val bbox = wapObject.boundingBox
        val backRect = Rectangle().apply {
            xProperty().bind(bbox.map { it.minX.toInt().toDouble() + 0.5 })
            yProperty().bind(bbox.map { it.minY.toInt().toDouble() + 0.5 })
            widthProperty().bind(bbox.map { it.width })
            heightProperty().bind(bbox.map { it.height })
            opacity = 0.2
        }

        selectToolContext.attachController { SelectionSurfaceController(backRect, it) }

        val imageView = ImageView().apply {
            xProperty().bind(
                    wapObject.rezImageMetadata.map { it!!.offset.x.toDouble() - it.size.width / 2 }.orElse(0.0))
            yProperty().bind(
                    wapObject.rezImageMetadata.map { it!!.offset.y.toDouble() - it.size.height / 2 }.orElse(0.0))

            translateXProperty().bind(wapObject.x)
            translateYProperty().bind(wapObject.y)

            imageProperty().bind(provideImage(wapObject))

            isMouseTransparent = true
        }

        moveToolContext.attachController { MoveToolObjectController(backRect, it) }

        return Group(backRect, imageView)
    }

    private fun provideImage(wapObject: WapObject) = observableValue {
        rezImageProvider.provideImage(
                resolveShortId(wapObject.imageSet.value), wapObject.i.value.toInt()
        )?.image
    }

    fun presentObjectUi(wapObject: WapObject): Node {
        val rectangle = entityRectangle(wapObject, wapObject.boundingBox, camera)
        return rectangle
    }
}

fun entityRectangle(entity: Entity, boundingBox: ObservableValue<BoundingBox>, camera: Camera): Rectangle {
    return transformedRectangle(boundingBox, camera.transform).apply {
        strokeProperty().bind(rectangleColor(entity))
        fill = Color.TRANSPARENT
        isMouseTransparent = true
    }
}

fun rectangleColor(entity: Entity): ObservableValue<Color> {
    return combine(
            entity.isHovered,
            entity.isSelected,
            entity.isPreselected
    ) { isHovered,
        isSelected,
        isPreselected ->
        when {
            isPreselected -> Color.ORANGE
            isSelected -> Color.RED
            isHovered -> Color.BLUE
            else -> Color.LIGHTBLUE
        }
    }
}