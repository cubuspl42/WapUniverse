package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group
import java.net.URL
import java.util.ResourceBundle

class WorldViewController(
        private val model: PlaneEditor,
        private val rezImageProvider: RezImageProvider
) : Initializable {
    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var planeRoot: Group

    private val plane = model.plane


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        wrapperPane.run {
            setOnMousePressed { e ->
                if (e.button == CAMERA_DRAG_MOUSE_BUTTON) {
                    CameraDragController(model, this, e)
                }
            }
        }
        planeRoot.run {
            translateXProperty().bind(model.cameraOffset.map { -it.x })
            translateYProperty().bind(model.cameraOffset.map { -it.y })
            children.add(createTilesGroup())
        }
    }

    private fun createTilesGroup() =
            group(plane.tiles.toObservableList { index, tileId ->
                ImageView().apply {
                    x = index.x * 64.0
                    y = index.y * 64.0
                    imageProperty().bind(provideTileImage(tileId))
                }
            })

    private fun provideTileImage(tileId: Int) =
            provideTileImage(rezImageProvider, plane.world.imageDir, plane.imageSet, tileId)
}
