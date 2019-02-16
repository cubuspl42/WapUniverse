package wapuniverse.app.world_preview.`object`

import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import org.reactfx.value.Val
import wapuniverse.app.RootWindow
import wapuniverse.app.world_preview.DoubleNode
import wapuniverse.app.world_preview.Rectangle
import wapuniverse.editor.WapObject
import wapuniverse.extensions.bindPosition

private typealias Ui = RootWindow

fun Ui.wapObject(wapObject: WapObject): DoubleNode {
    val rezImage = Val.combine(wapObject.fqImageSetId, wapObject.i) { fqImageSetIdNow, iNow ->
        rezImageCache.getImage(fqImageSetIdNow!!, iNow)
    }
    val image = rezImage.map { it!!.image }
    val boundingBox = wapObject.boundingBox
    return DoubleNode(
            ImageView().apply {
                imageProperty().bind(image)
                bindPosition(boundingBox.map { it.position })
                opacityProperty().bind(opacityValue(wapObject))
            },
            Group(
                    Rectangle(boundingBox).apply {
                        fill = Color.TRANSPARENT
                        strokeProperty().bind(wapObjectStrokeColor(wapObject))
                        opacityProperty().bind(opacityValue(wapObject))
                    }
            )
    )
}

fun opacityValue(wapObject: WapObject) =
        wapObject.isActive.map {
            if (it) 1.0
            else 0.2
        }!!

private fun wapObjectStrokeColor(wapObject: WapObject) =
        Val.combine(
                wapObject.isHighlighted,
                wapObject.isSelected
        ) { isHighlightedNow, isSelectedNow ->
            if (isHighlightedNow || isSelectedNow) Color.RED else Color.LIGHTBLUE
        }
