package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableSet
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import wapuniverse.model.PlaneEditor
import wapuniverse.model.WapObject
import wapuniverse.rez.RezImageProvider
import wapuniverse.util.map
import wapuniverse.util.plus
import wapuniverse.view.canvasscene.CanvasNode
import wapuniverse.view.canvasscene.CanvasScene
import wapuniverse.view.canvasscene.PlaneNode
import wapuniverse.view.canvasscene.TileMapNode
import wapuniverse.view.canvasscene.WapObjectNode
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
    lateinit var stackPane: Pane

    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var borderPane: BorderPane

    private val plane = planeEditor.plane

    private val pencilToolContext = planeEditor.pencilToolContext

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        wrapperPane.run {
            setOnMousePressed { e ->
                if (e.button == CAMERA_DRAG_MOUSE_BUTTON) {
                    CameraDragController(planeEditor, this, e)
                }
            }
        }

        wrapperPane.children.add(canvasScene())

        planeEditor.selectToolContext.forEach {
            WorldViewSelectToolContextController(wrapperPane, planeEditor, it)
        }

        planeEditor.pencilToolContext.forEach {
            WorldViewPencilToolContextController(planeEditor, wrapperPane, it)
        }

        borderPane.bottomProperty().bind(planeEditor.pencilToolContext.map {
            tilePicker(it, rezImageProvider)
        })
    }

    private fun canvasScene(): CanvasScene {
        val tiles = TileMapNode(plane, rezImageProvider) as CanvasNode
        val wapObjects = plane.wapObjects.map {
            WapObjectNode(it, rezImageProvider) as CanvasNode
        }
        val children = observableSet(tiles) + wapObjects
        return CanvasScene(planeEditor, PlaneNode(planeEditor, children))
    }

    private fun createPencilToolUi() = group(pencilToolContext.map { pencilToolContext1 ->
        Rectangle(64.0, 64.0).apply {
            xProperty().bind(pencilToolContext1.cursorOffset.map { it.x * 64.0 })
            yProperty().bind(pencilToolContext1.cursorOffset.map { it.y * 64.0 })
            stroke = Color.DARKRED
            fill = Color.TRANSPARENT
            isMouseTransparent = true
        } as Node
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
