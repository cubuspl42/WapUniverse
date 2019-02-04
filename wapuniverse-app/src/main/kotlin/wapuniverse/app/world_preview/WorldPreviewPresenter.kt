package wapuniverse.app.world_preview

import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import wapuniverse.app.EditorContext
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.AreaSelectionContext
import wapuniverse.editor.WapObject
import wapuniverse.editor.extensions.*
import wapuniverse.extensions.group
import wapuniverse.extensions.listBind
import wapuniverse.extensions.map
import wapuniverse.rez.RezImageCache

class WorldPreviewPresenter(
        private val rezImageCache: RezImageCache
) {
    fun root(editorContext: EditorContext): Pane {
        val activePlaneContext = editorContext.editor.activePlaneContext

        val worldPreviewPane = pane(activePlaneContext.map { plane(it) }).apply {
            clip = fullClip(this)
        }.apply {
            setOnMouseClicked { requestFocus() }
        }

        activePlaneContext.forEach { ActivePlaneController(it, worldPreviewPane) }

        return worldPreviewPane
    }

    private fun plane(activePlaneContext: ActivePlaneContext) = Group(
            doubleGroup(activePlaneContext.plane.objects.map { wapObject(it) }),
            group(activePlaneContext.areaSelectionContext.map { areaSelectionRect(it) }),
            inputHandler(activePlaneContext)
    ).apply {
        translateXProperty().bind(activePlaneContext.cameraPosition.map { -it.x })
        translateYProperty().bind(activePlaneContext.cameraPosition.map { -it.y })
    }

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
            xProperty().bind(area.map { it.minX })
            yProperty().bind(area.map { it.minY })
            widthProperty().bind(area.map { it.width })
            heightProperty().bind(area.map { it.height })
            fill = Color.RED
            opacity = 0.5
        }
    }

    private fun wapObject(wapObject: WapObject): DoubleNode {
        val rezImage = rezImageCache.getImage(wapObject.imageSet, -1)!!
        val image = rezImage.image!!
        val x = wapObject.positionInit.x.toDouble()
        val y = wapObject.positionInit.y.toDouble()
        val w = image.width
        val h = image.height
        val bb = wapObject.boundingBox
        return DoubleNode(
                ImageView(rezImage.image).apply {
                    this.x = x
                    this.y = y
                },
                Group(
                        Rectangle(x, y, w, h).apply {
                            fill = Color.TRANSPARENT
                            strokeProperty().bind(wapObject.isHighlighted.map {
                                if (it) Color.RED else Color.ORANGE
                            })
                        },
                        Rectangle(bb.minX, bb.minY, bb.width, bb.height)
                )
        )
    }
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
