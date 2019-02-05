package wapuniverse.app.world_preview

import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.reactfx.value.Val
import wapuniverse.app.EditorContext
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.AreaSelectionContext
import wapuniverse.editor.Plane
import wapuniverse.editor.WapObject
import wapuniverse.editor.extensions.forEach
import wapuniverse.editor.extensions.map
import wapuniverse.extensions.group
import wapuniverse.extensions.listBind
import wapuniverse.extensions.map
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i
import wapuniverse.rez.RezImageCache

class WorldPreviewPresenter(
        private val rezImageCache: RezImageCache
) {
    fun root(editorContext: EditorContext): Pane {
        val activePlaneContext = editorContext.editor.activePlaneContext

        val previewPane = Pane().apply {
            clip = fullClip(this)
        }.apply {
            setOnMouseClicked { requestFocus() }
            listBind(children, activePlaneContext.map { plane(it, this) })
        }

        activePlaneContext.forEach { ActivePlaneController(it, previewPane) }

        return previewPane
    }

    private fun plane(activePlaneContext: ActivePlaneContext, previewPane: Pane): Node? = Group(
            TilesCanvas(activePlaneContext, rezImageCache, previewPane),
            Group(
                    doubleGroup(activePlaneContext.plane.objects.map { wapObject(it) }),
                    group(activePlaneContext.areaSelectionContext.map { areaSelectionRect(it) }),
                    inputHandler(activePlaneContext)
            ).apply {
                translateXProperty().bind(activePlaneContext.cameraPosition.map { -it.x })
                translateYProperty().bind(activePlaneContext.cameraPosition.map { -it.y })
            }
    )

    private fun inputHandler(activePlaneContext: ActivePlaneContext): Node? {
        val a = 262144.0
        return Rectangle(0.0, 0.0, a, a).apply {
            InputHandlerController(activePlaneContext, this)
            fill = Color.TRANSPARENT
        }
    }

    private fun areaSelectionRect(areaSelectionContext: AreaSelectionContext): Node {
        val area = areaSelectionContext.area
        return Rectangle().apply {
            bind(area)
            fill = Color.RED
            opacity = 0.5
        }
    }

    private fun wapObject(wapObject: WapObject): DoubleNode {
        val rezImage = wapObject.fqImageSetId.map { rezImageCache.getImage(it, -1) }
        val image = rezImage.map { it?.image }
        val boundingBox = wapObject.boundingBox
        return DoubleNode(
                ImageView().apply {
                    imageProperty().bind(image)
                    bindPosition(boundingBox.map { it.position })
                },
                Group(
                        Rectangle(boundingBox).apply {
                            fill = Color.TRANSPARENT
                            strokeProperty().bind(wapObject.isHighlighted.map {
                                if (it) Color.RED else Color.ORANGE
                            })
                        }
                )
        )
    }
}

fun Rectangle(rect: Val<Rect2i>) = Rectangle().apply {
    xProperty().bind(rect.map { it.position.x })
    yProperty().bind(rect.map { it.position.y })
    widthProperty().bind(rect.map { it.size.width })
    heightProperty().bind(rect.map { it.size.height })
}

private fun ImageView.bindPosition(position: Val<Vec2i>) {
    xProperty().bind(position.map { it.x })
    yProperty().bind(position.map { it.y })
}

private fun Rectangle.bind(rect: Val<Rect2i>) {
    xProperty().bind(rect.map { it.position.x })
    yProperty().bind(rect.map { it.position.y })
    widthProperty().bind(rect.map { it.size.width })
    heightProperty().bind(rect.map { it.size.height })
}

class InputHandlerController(
        activePlaneContext: ActivePlaneContext,
        node: Node
) : Controller(activePlaneContext, { node.scene }) {
    init {
        subscribe(dragGesturesOf(node)) { dragGesture ->
            val position = dragGesture.position.map { it.toVec2i() }
            val areaSelectionContext = activePlaneContext.selectByArea(position)
            dragGesture.onEnded.subscribe { areaSelectionContext.commit() }
        }
    }
}

fun pane(child: ObservableValue<Node?>) = Pane().apply {
    properties[child] = child
    listBind(this.children, child)
}

fun fullClip(pane: Pane) = Rectangle().apply {
    widthProperty().bind(pane.widthProperty())
    heightProperty().bind(pane.heightProperty())
}

private fun text(textValue: ObservableValue<String>) = Text().apply {
    textProperty().bind(textValue)
}
