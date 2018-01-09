package wapuniverse.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Affine
import wapuniverse.geom.Vec2d
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.view.ext.hoverPositionProperty
import wapuniverse.view.ext.map
import wapuniverse.view.ext.parentToLocal
import wapuniverse.view.ext.position
import wapuniverse.view.ext.toVec2d
import java.net.URL
import java.util.ResourceBundle

class WorldViewController(
        private val root: Group,
        private val uiNode: Group,
        private val camera: Camera,
        private val editorContext: EditorContext
) : Initializable {
    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var contentPane: Pane

    @FXML
    lateinit var uiPane: Group

    var scale = 1.0

    var dragConstraint: Vec2d? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        contentPane.children.add(root)
        uiPane.children.add(uiNode)

        camera.transform.addListener { observable, oldValue, newValue ->
            root.transforms.setAll(newValue)
        }

        wrapperPane.clip = Rectangle().apply {
            widthProperty().bind(wrapperPane.widthProperty())
            heightProperty().bind(wrapperPane.heightProperty())
        }

        wrapperPane.addEventFilter(MouseEvent.MOUSE_PRESSED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                dragConstraint = root.parentToLocal(ev.position)
                ev.consume()
            }
        })

        wrapperPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                transformSpace(ev.position, dragConstraint!!)
                ev.consume()
            }
        })

        wrapperPane.addEventFilter(MouseEvent.MOUSE_RELEASED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                ev.consume()
            }
        })

        wrapperPane.addEventFilter(ScrollEvent.SCROLL, { ev ->
            val scaleMultiplier = when {
                ev.deltaY > 0 -> 2.0
                else -> 0.5
            }

            scale *= scaleMultiplier

            transformSpace(ev.position, root.parentToLocal(ev.position))
            ev.consume()
        })

        editorContext.hoverPositionProperty.bind(wrapperPane.hoverPositionProperty().map {
            it?.let { viewToWorld(it.toVec2d()) }
        })

        wrapperPane.addEventFilter(MouseEvent.MOUSE_CLICKED) { ev ->
            if (ev.button == MouseButton.PRIMARY && ev.isStillSincePress) {
                (editorContext.activeToolContext as? SelectToolContext)?.selectObjectsAt(viewToWorld(ev.position))
            }
        }

        uiPane.setOnMousePressed {
            println()
        }
    }

    private fun viewToWorld(it: Vec2d) =
            root.parentToLocal(it)

    private fun transformSpace(viewConstraint: Vec2d, worldConstraint: Vec2d) {
        val translate = -(worldConstraint - (viewConstraint / scale))

        val affine = Affine()
        affine.appendScale(scale, scale)
        affine.appendTranslation(translate.x, translate.y)

        camera.transform.set(affine)
    }
}

