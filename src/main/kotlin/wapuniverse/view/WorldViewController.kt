package wapuniverse.view

import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Affine
import org.fxmisc.easybind.EasyBind.listBind
import wapuniverse.geom.Vec2d
import wapuniverse.model.EditorContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.selectToolContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.hoverPositionProperty
import wapuniverse.view.ext.map
import wapuniverse.view.ext.parentToLocal
import wapuniverse.view.ext.position
import wapuniverse.view.ext.singletonObservableList
import wapuniverse.view.ext.toVec2d
import java.net.URL
import java.util.ResourceBundle

class WorldViewController(
        private val editorContext: EditorContext,
        private val rezImageProvider: RezImageProvider
) : Initializable {
    private val world = editorContext.world

    @FXML
    lateinit var wrapperPane: Pane

    @FXML
    lateinit var contentPane: Pane

    @FXML
    lateinit var contentGroup: Group

    @FXML
    lateinit var uiGroup: Group

    @FXML
    lateinit var objectsUiGroup: Group

    @FXML
    lateinit var editorUiGroup: Group


    var scale = 1.0

    var dragConstraint: Vec2d? = null

    private val camera = Camera()

    private val wapObjectPresenter = WapObjectPresenter(rezImageProvider, camera, editorContext)

    private val selectionAreaRectangle = presentSelectionAreaRectangle()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setupCameraController()

        camera.transform.addListener { observable, oldValue, newValue ->
            contentGroup.transforms.setAll(newValue)
        }

        wrapperPane.clip = Rectangle().apply {
            widthProperty().bind(wrapperPane.widthProperty())
            heightProperty().bind(wrapperPane.heightProperty())
        }

        editorContext.hoverPositionProperty.bind(wrapperPane.hoverPositionProperty().map {
            it?.let { viewToWorld(it.toVec2d()) }
        })

        wrapperPane.addEventFilter(MouseEvent.MOUSE_CLICKED) { ev ->
            if (ev.button == MouseButton.PRIMARY && ev.isStillSincePress) {
                (editorContext.activeToolContext as? SelectToolContext)?.selectObjectsAt(viewToWorld(ev.position))
            }
        }

        listBind(
                contentGroup.children,
                world.objects.map { wapObjectPresenter.presentObjectImageView(it) }
        )

        listBind(
                objectsUiGroup.children,
                world.objects.map { wapObjectPresenter.presentObjectUi(it) }
        )

        listBind(editorUiGroup.children, singletonObservableList(selectionAreaRectangle))
    }

    private fun presentSelectionAreaRectangle(): ObservableValue<Node> {
        return editorContext.selectToolContext
                .flatMap { it.areaSelection }
                .map {
                    presentRectangle(it.boundingBox, camera.transform).apply {
                        fill = Color.NAVY
                        stroke = Color.CYAN
                        opacity = 0.3
                    } as Node
                }
    }

    private fun setupCameraController() {
        wrapperPane.addEventFilter(MouseEvent.MOUSE_PRESSED, { ev ->
            if (ev.button == MouseButton.SECONDARY) {
                dragConstraint = contentGroup.parentToLocal(ev.position)
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

            transformSpace(ev.position, contentGroup.parentToLocal(ev.position))
            ev.consume()
        })
    }

    private fun viewToWorld(it: Vec2d) =
            contentGroup.parentToLocal(it)

    private fun transformSpace(viewConstraint: Vec2d, worldConstraint: Vec2d) {
        val translate = -(worldConstraint - (viewConstraint / scale))

        val affine = Affine()
        affine.appendScale(scale, scale)
        affine.appendTranslation(translate.x, translate.y)

        camera.transform.set(affine)
    }
}
