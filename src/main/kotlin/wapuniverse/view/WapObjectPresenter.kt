package wapuniverse.view

import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.model.WapObject
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.map
import wapuniverse.view.util.observableValue

class WapObjectPresenter(
        private val rezImageProvider: RezImageProvider,
        private val camera: Camera
) {
    fun presentObjectImageView(wapObject: WapObject) =
            ImageView().apply {
                xProperty().bind(wapObject.boundingBox.map { it.minX })
                yProperty().bind(wapObject.boundingBox.map { it.minY })
                imageProperty().bind(provideImage(wapObject))
            }

    private fun provideImage(wapObject: WapObject) = observableValue {
        rezImageProvider.provideImage(
                wapObject.imageSet.value, -1
        )?.image
    }

    fun presentObjectUi(wapObject: WapObject): Node {
        val bounds = combine(wapObject.boundingBox, camera.transform) { bounds, transform ->
            transform.transform(bounds)
        }
        return Rectangle().apply {
            xProperty().bind(bounds.map { it.minX.toInt().toDouble() + 0.5 })
            yProperty().bind(bounds.map { it.minY.toInt().toDouble() + 0.5 })
            widthProperty().bind(bounds.map { it.width })
            heightProperty().bind(bounds.map { it.height })

            strokeProperty().bind(wapObject.isHovered.map { isHovered ->
                if (isHovered) Color.BLUE else Color.RED
            })

            fill = Color.TRANSPARENT
        }
    }
}

