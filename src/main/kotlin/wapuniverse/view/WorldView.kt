package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Affine
import wapuniverse.geom.Vec2d
import wapuniverse.view.ext.parentToLocal
import wapuniverse.view.ext.position
import wapuniverse.view.util.loadFxml
import java.net.URL
import java.util.ResourceBundle

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldViewController(
        private val root: Group
) : Initializable {
    @FXML
    lateinit var pane: Pane

    var scale = 1.0

    var dragConstraint: Vec2d? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        pane.children.add(root)

        val clipRect = Rectangle()
        clipRect.widthProperty().bind(pane.widthProperty())
        clipRect.heightProperty().bind(pane.heightProperty())
        pane.clip = clipRect

        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                dragConstraint = root.parentToLocal(ev.position)
                ev.consume()
            }
        })

        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                transformSpace(ev.position, dragConstraint!!)
                ev.consume()
            }
        })

        pane.addEventFilter(MouseEvent.MOUSE_RELEASED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                ev.consume()
            }
        })

        pane.addEventFilter(ScrollEvent.SCROLL, { ev ->
            val scaleMultiplier = when {
                ev.deltaY > 0 -> 2.0
                else -> 0.5
            }

            scale *= scaleMultiplier

            transformSpace(ev.position, root.parentToLocal(ev.position))
            ev.consume()
        })
    }

    private fun transformSpace(viewConstraint: Vec2d, worldConstraint: Vec2d) {
        val translate = -(worldConstraint - (viewConstraint / scale))

        val affine = Affine()
        affine.appendScale(scale, scale)
        affine.appendTranslation(translate.x, translate.y)

        root.transforms.setAll(affine)
    }
}

fun presentWorldView(root: Group) =
        loadFxml(fxmlFilename) { WorldViewController(root) }
