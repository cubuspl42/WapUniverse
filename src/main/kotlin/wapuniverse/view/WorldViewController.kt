package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.model.PlaneEditor
import wapuniverse.model.WapObject
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.point
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group
import wapuniverse.view.util.observableValue
import java.net.URL
import java.util.ResourceBundle

class WorldViewController(
        private val model: PlaneEditor,
        private val rezImageProvider: RezImageProvider
) : Initializable {
    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var uiRoot: Group

    @FXML
    lateinit var planeRoot: Group

    private val plane = model.plane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        wrapperPane.run {
            setOnMouseClicked { e ->
                if (e.button == MouseButton.PRIMARY) {
                    model.selectObjects(e.point.toVec2i())
                }
            }
            setOnMousePressed { e ->
                if (e.button == CAMERA_DRAG_MOUSE_BUTTON) {
                    CameraDragController(model, this, e)
                }
            }
        }
        uiRoot.run {
            translateXProperty().bind(model.cameraOffset.map { -it.x })
            translateYProperty().bind(model.cameraOffset.map { -it.y })
            children.addAll(createObjectsUi())
        }
        planeRoot.run {
            translateXProperty().bind(model.cameraOffset.map { -it.x })
            translateYProperty().bind(model.cameraOffset.map { -it.y })
            children.addAll(createTilesGroup(), createObjectsGroup())
        }
    }

    private fun createTilesGroup() =
            group(plane.tiles.toObservableList { index, tileId ->
                ImageView().apply {
                    x = index.x * 64.0
                    y = index.y * 64.0
                    imageProperty().bind(provideImage(plane.findTileImageMetadata(tileId)?.rezPath))
                }
            })

    private fun createObjectsUi()  =
            group(plane.wapObjects.toObservableList { wapObject ->
                wapObjectUi(wapObject)
            })

    private fun createObjectsGroup() =
            group(plane.wapObjects.toObservableList { wapObject ->
                wapObjectImage(wapObject)
            })


    private fun wapObjectUi(wapObject: WapObject): Node {
        val b = wapObject.bounds
        return Rectangle(b.minX, b.minY, b.width, b.height).apply {
            stroke = Color.RED
            fill = Color.TRANSPARENT
            isMouseTransparent = true
        }
    }

    private fun wapObjectImage(wapObject: WapObject): Node {
        val s = Vec2i(if (wapObject.mirrored) -1 else 1, if (wapObject.inverted) -1 else 1)
        return ImageView().apply {
            x = wapObject.imageDiagonal.a.x.toDouble()
            y = wapObject.imageDiagonal.a.y.toDouble()
            scaleX = s.x.toDouble()
            scaleY = s.y.toDouble()
            imageProperty().bind(provideImage(wapObject.imageMetadata?.rezPath))
        }
    }

    private fun provideImage(rezPath: String?): ObservableValue<Image?> {
        return observableValue { rezPath?.let { rezImageProvider.provideImage(it) } }
    }
}