package wapuniverse.app

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.EasyBind.listBind
import wapuniverse.editor.ActivePlaneContext
import wapuniverse.editor.WapObject
import wapuniverse.editor.extensions.map
import wapuniverse.editor.extensions.subscribe
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
            group(activePlaneContext.plane.objects.map { wapObject(it) })

    private fun wapObject(wapObject: WapObject) =
            ImageView(rezImageCache.getImage(wapObject.imageSet, -1)!!.image).apply {
                x = wapObject.position.x.toDouble()
                y = wapObject.position.y.toDouble()
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

private fun group(children: ObservableList<out Node>) = Group().apply {
    properties[children] = children
    listBind(this.children, children)
}

private fun <E, R> ObservableList<E>.map(function: (E) -> R): ObservableList<R> {
    return EasyBind.map(this) { function(it)!! }
}

private fun text(textValue: ObservableValue<String>) = Text().apply {
    textProperty().bind(textValue)
}
