package wapuniverse.app.worldPreview

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import wapuniverse.app.EditorContext
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.WapObject
import wapuniverse.editor.extensions.map
import wapuniverse.editor.extensions.subscribe
import wapuniverse.extensions.map
import wapuniverse.rez.RezImageCache

class WorldPreviewPresenter(
        private val rezImageCache: RezImageCache
) {
    fun root(editorContext: EditorContext): Pane {
        val activePlaneContext = editorContext.editor.activePlaneContext
        return pane(activePlaneContext.map { plane(it) }).apply {
            clip = fullClip(this)
        }
    }

    private fun plane(activePlaneContext: ActivePlaneContext) =
            doubleGroup(activePlaneContext.plane.objects.map { wapObject(it) })

    private fun wapObject(wapObject: WapObject): DoubleNode {
        val rezImage = rezImageCache.getImage(wapObject.imageSet, -1)!!
        val image = rezImage.image!!
        val x = wapObject.position.x.toDouble()
        val y = wapObject.position.y.toDouble()
        val w = image.width
        val h = image.height
        return DoubleNode(
                ImageView(rezImage.image).apply {
                    this.x = x
                    this.y = y
                },
                Rectangle(x, y, w, h).apply {
                    fill = Color.TRANSPARENT
                    stroke = Color.RED
                }
        )
    }
}

private fun pane(child: ObservableValue<Node?>) = Pane().apply {
    properties[child] = child
    listBind(this.children, child)
}

private fun <E : Any> listBind(list: ObservableList<E?>, child: ObservableValue<E?>) {
    list.setOneNullable(child.value)
    child.subscribe {
        list.setOneNullable(it)
    }
}

private fun <E> ObservableList<E>.setOneNullable(value: E) {
    value?.let { setAll(it) } ?: clear()
}

private fun fullClip(pane: Pane) = Rectangle().apply {
    widthProperty().bind(pane.widthProperty())
    heightProperty().bind(pane.heightProperty())
}

private fun text(textValue: ObservableValue<String>) = Text().apply {
    textProperty().bind(textValue)
}
