package wapuniverse.view

import javafx.beans.value.ObservableObjectValue
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import org.reactfx.EventStreams.eventsOf
import org.reactfx.Subscription
import wapuniverse.geom.Vec2d
import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.map
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group
import wapuniverse.view.util.observableValue

fun worldView(planeEditor: PlaneEditor, rezImageProvider: RezImageProvider): Node {
    val plane = planeEditor.plane
    val tileNodes = plane.tiles.toObservableList { index, tileId ->
        ImageView().apply {
            x = index.x * 64.0
            y = index.y * 64.0
            imageProperty().bind(provideTileImage(rezImageProvider, plane.world.imageDir, plane.imageSet, tileId))
        }
    }
    return Pane(group(tileNodes).apply {
        translateXProperty().bind(planeEditor.cameraOffset.map { -it.x })
        translateYProperty().bind(planeEditor.cameraOffset.map { -it.y })
    }).apply {
        setOnMousePressed { e1 ->
            if (e1.button == MouseButton.SECONDARY) {
                val cameraStartPosition = planeEditor.cameraOffset.value
                val startPosition = Vec2d(e1.x, e1.y)
                val s1 = eventsOf(this, MouseEvent.MOUSE_DRAGGED).subscribe { e2 ->
                    val newPosition = Vec2d(e2.x, e2.y)
                    val delta = newPosition - startPosition
                    planeEditor.cameraOffset.value = cameraStartPosition - delta.toVec2i()
                }
                var s2: Subscription? = null
                s2 = eventsOf(this, MouseEvent.MOUSE_RELEASED).subscribe {
                    s1.unsubscribe()
                    s2!!.unsubscribe()
                }
            }
        }
    }
}

fun provideTileImage(
        rezImageProvider: RezImageProvider,
        imageDir: String,
        imageSet: String,
        i: Int
): ObservableObjectValue<Image?> {
    val prefix = imageDir.replace('\\', '_').removePrefix("_")
    val imageSetId = "${prefix}_$imageSet"
    return observableValue { rezImageProvider.provideImage(imageSetId, i.coerceAtLeast(0))?.image }
}
