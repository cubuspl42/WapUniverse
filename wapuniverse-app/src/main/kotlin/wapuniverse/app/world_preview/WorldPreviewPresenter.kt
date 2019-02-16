package wapuniverse.app.world_preview

import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.reactfx.value.Val
import wapuniverse.app.EditorContext
import wapuniverse.app.RootWindow
import wapuniverse.app.tilePicker
import wapuniverse.editor.*
import wapuniverse.editor.extensions.flatMap
import wapuniverse.editor.extensions.forEach
import wapuniverse.editor.extensions.map
import wapuniverse.extensions.group
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Size2i
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.util.bindChild
import wapuniverse.util.fullClip

typealias Ui = RootWindow

fun worldPreviewUi(rootWindow: RootWindow, editorContext: EditorContext) =
        rootWindow.root(editorContext)

fun Ui.root(editorContext: EditorContext): Pane {
    val activePlaneContext = editorContext.editor.activePlaneContext
    val objectModeContext = activePlaneContext.flatMap { it.objectModeContext }
    val tileModeContext = activePlaneContext.flatMap { it.tileModeContext }

    val previewPane = Pane().apply {
        bindChild(activePlaneContext.map { plane(it, this) })
        clip = fullClip(this)
        setOnMouseClicked { requestFocus() }
    }

    activePlaneContext.forEach { ActivePlaneController(it, previewPane) }
    objectModeContext.forEach { ObjectModeController(it, previewPane) }
    tileModeContext.forEach { TileModeController(it, previewPane) }

    return StackPane(
            previewPane,
            BorderPane().apply {
                bottomProperty().bind(tileModeContext.map { tilePicker(it!!, rezImageCache) })
                isPickOnBounds = false
            }
    )
}

private fun Ui.plane(activePlaneContext: ActivePlaneContext, previewPane: Pane): Node? {
    val objectModeContext = activePlaneContext.objectModeContext
    return Group(
            Canvas().apply {
                TilesCanvasController(activePlaneContext, rezImageCache, this, previewPane)
            },
            Group(
                    doubleGroup(activePlaneContext.plane.objects.map { wapObject(it) }),
                    modeUserInterface(activePlaneContext.modeContext),
                    areaSelectionGroup(objectModeContext.flatMap { it!!.areaSelectionContext }),
                    inputHandler(activePlaneContext)
            ).apply {
                translateXProperty().bind(activePlaneContext.cameraPosition.map { -it.x })
                translateYProperty().bind(activePlaneContext.cameraPosition.map { -it.y })
            }
    )
}

private fun modeUserInterface(modeContext: Val<ModeContext>): Node {
    return group(modeContext.map {
        when (it) {
            is ObjectModeContext -> areaSelectionGroup(it.areaSelectionContext)
            is TileModeContext -> tileCursor(it)
            else -> throw RuntimeException()
        }
    })
}

private fun tileCursor(context: TileModeContext) =
        Rectangle(context.tileCursor.map(::tileRect))
                .apply {
                    stroke = Color.DARKGRAY
                    strokeWidth = 4.0
                    opacity = 0.7
                    fill = Color.TRANSPARENT
                }

private fun areaSelectionGroup(areaSelectionContext: Val<AreaSelectionContext?>) =
        group(areaSelectionContext.map { areaSelectionRect(it!!) })

private fun inputHandler(activePlaneContext: ActivePlaneContext) =
        group(activePlaneContext.modeContext.map {
            when (it) {
                is ObjectModeContext -> objectModeInputHandler(it)
                is TileModeContext -> tileModeInputHandler(it)
                else -> throw RuntimeException()
            }
        })

private fun objectModeInputHandler(context: ObjectModeContext) = inputRectangle {
    dragGesturesOf(this).subscribe { dragGesture ->
        val position = dragGesture.position.map { it.toVec2i() }
        DragGestureController(context.selectByArea(position), dragGesture)
    }
}

private fun tileModeInputHandler(context: TileModeContext) = inputRectangle {
    setOnMouseClicked { e ->
        if (e.button == MouseButton.PRIMARY) {
            context.tileCursor.value = positionToTileOffset(e.position)
        }
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

private fun Ui.wapObject(wapObject: WapObject): DoubleNode {
    val rezImage = Val.combine(wapObject.fqImageSetId, wapObject.i) { fqImageSetIdNow, iNow ->
        rezImageCache.getImage(fqImageSetIdNow!!, iNow)
    }
    val image = rezImage.map { it!!.image }
    val boundingBox = wapObject.boundingBox
    return DoubleNode(
            ImageView().apply {
                imageProperty().bind(image)
                bindPosition(boundingBox.map { it.position })
            },
            Group(
                    Rectangle(boundingBox).apply {
                        fill = Color.TRANSPARENT
                        strokeProperty().bind(wapObjectStrokeColor(wapObject))
                    }
            )
    )
}

private fun tileRect(offset: Vec2i) =
        Rect2i(offset * tileLength, Size2i(tileLength, tileLength))

val MouseEvent.position: Vec2d
    get() = Vec2d(x, y)

private fun positionToTileOffset(position: Vec2d) =
        position.toVec2i() / tileLength

private fun inputRectangle(controller: Node.() -> Unit): Node {
    val a = 262144.0
    return Rectangle(0.0, 0.0, a, a).apply {
        controller(this)
        fill = Color.TRANSPARENT
    }
}

private fun wapObjectStrokeColor(wapObject: WapObject) =
        Val.combine(
                wapObject.isHighlighted,
                wapObject.isSelected
        ) { isHighlightedNow, isSelectedNow ->
            if (isHighlightedNow || isSelectedNow) Color.RED else Color.LIGHTBLUE
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

class DragGestureController(
        areaSelectionContext: AreaSelectionContext,
        dragGesture: DragGesture
) : Controller(areaSelectionContext) {
    init {
        subscribe(dragGesture.onEnded) { areaSelectionContext.commit() }
    }
}

private fun text(textValue: ObservableValue<String>) = Text().apply {
    textProperty().bind(textValue)
}
