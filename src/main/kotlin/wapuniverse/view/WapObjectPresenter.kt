package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.model.EditorContext
import wapuniverse.model.WapObject
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

    fun presentObjectImageView(wapObject: WapObject): ImageView {
        val imageView = ImageView().apply {
            xProperty().bind(wapObject.boundingBox.map { it.minX })
            yProperty().bind(wapObject.boundingBox.map { it.minY })
            imageProperty().bind(provideImage(wapObject))
        }

        selectToolContext.attachController { SelectionSurfaceController(imageView, it) }

        moveToolContext.attachController { MoveToolObjectController(imageView, it) }

        return imageView
    }

    private fun provideImage(wapObject: WapObject) = observableValue {
        rezImageProvider.provideImage(
                wapObject.imageSet.value, -1
        )?.image
    }

    fun presentObjectUi(wapObject: WapObject): Node {
        val rectangle = presentRectangle(wapObject.boundingBox, camera.transform).apply {
            strokeProperty().bind(rectangleColor(wapObject))
            fill = Color.TRANSPARENT
            isMouseTransparent = true
        }
        return rectangle
    }

    private fun rectangleColor(wapObject: WapObject): ObservableValue<Color> {
        return combine(
                wapObject.isHovered,
                wapObject.isSelected,
                wapObject.isPreselected
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
}
