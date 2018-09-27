package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import wapuniverse.model.PlaneEditor
import wapuniverse.model.WapObject
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.flatMap
import wapuniverse.view.extensions.forEach
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group
import wapuniverse.view.util.observableValue
import java.net.URL
import java.util.ResourceBundle

class WorldViewController(
        private val planeEditor: PlaneEditor,
        private val rezImageProvider: RezImageProvider
) : Initializable {
    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var uiRoot: Group

    @FXML
    lateinit var planeRoot: Group

    private val plane = planeEditor.plane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        wrapperPane.run {
            setOnMousePressed { e ->
                if (e.button == CAMERA_DRAG_MOUSE_BUTTON) {
                    CameraDragController(planeEditor, this, e)
                }
            }
        }
        uiRoot.run {
            translateXProperty().bind(planeEditor.cameraOffset.map { -it.x })
            translateYProperty().bind(planeEditor.cameraOffset.map { -it.y })
            children.addAll(createObjectsUi())
        }
        planeRoot.run {
            translateXProperty().bind(planeEditor.cameraOffset.map { -it.x })
            translateYProperty().bind(planeEditor.cameraOffset.map { -it.y })
            children.addAll(createTilesGroup(), createObjectsGroup())
        }

        planeEditor.selectToolContext.forEach {
            WorldViewSelectToolContextController(wrapperPane, planeEditor, it)
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

    private fun createObjectsUi() =
            group(plane.wapObjects.toObservableList { wapObject ->
                wapObjectUi(wapObject)
            })

    private fun createObjectsGroup() =
            group(plane.wapObjects.toObservableList { wapObject ->
                wapObjectImage(wapObject)
            })


    private fun wapObjectUi(wapObject: WapObject): Node {
        val b = wapObject.bounds
        return Rectangle().apply {
            strokeProperty().bind(wapObjectStrokeColor(wapObject))
            xProperty().bind(b.map { it.minX })
            yProperty().bind(b.map { it.minY })
            widthProperty().bind(b.map { it.width })
            heightProperty().bind(b.map { it.height })
            fill = Color.TRANSPARENT
            isMouseTransparent = true
        }
    }

    private fun wapObjectImage(wapObject: WapObject): Node {
        val sX = wapObject.mirrored.map { if (it) -1 else 1 }
        val sY = wapObject.inverted.map { if (it) -1 else 1 }
        val d = wapObject.imageDiagonal
        return ImageView().apply {
            xProperty().bind(d.map { it.a.x.toDouble() })
            yProperty().bind(d.map { it.a.y.toDouble() })
            scaleXProperty().bind(sX)
            scaleYProperty().bind(sY)
            imageProperty().bind(wapObject.imageMetadata.flatMap { provideImage(it.rezPath) })
        }
    }

    private fun provideImage(rezPath: String?): ObservableValue<Image?> {
        return observableValue { rezPath?.let { rezImageProvider.provideImage(it) } }
    }
}

private fun wapObjectStrokeColor(wapObject: WapObject) =
        wapObject.isSelected.map {
            if (it) Color.RED else Color.LIGHTBLUE
        }
