package wapuniverse.editor

import io.github.jwap32.v1.WwdObject
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.unmodifiableObservableMap
import javafx.collections.ObservableMap
import org.reactfx.value.Val
import org.reactfx.value.Val.combine
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i
import wapuniverse.editor.WapObjectIntAttrKey as IntKey
import wapuniverse.editor.WapObjectStringAttrKey as StrKey

class WapObject(
        val plane: Plane,
        wwdObject: WwdObject
) {
    data class ExportedAttrs(val attributes: Map<WapObjectAttrKey<*>, Any>)

    private val world = plane.world

    private val attributes = observableHashMap<WapObjectAttrKey<*>, Any>()

    fun <T : Any> getAttr(attrKey: WapObjectAttrKey<T>) =
            attributes.valAt(attrKey).map { uncheckedCast<Any, T>(it) }!!

    internal fun <T> setAttr(attrKey: WapObjectAttrKey<T>, value: T) {
        attributes[attrKey] = value
    }

    internal fun exportAttrs() = ExportedAttrs(attributes.toMap())

    internal fun importAttrs(exportedAttrs: ExportedAttrs) {
        attributes.clear()
        exportedAttrs.attributes.forEach { k, v ->
            attributes[k] = v
        }
    }

//    val intAttributes = AttributeMap<Int>()
//
//    val stringAttributes = AttributeMap<String>()

//    private val positionVar = newSimpleVar(Vec2i(wwdObject.x, wwdObject.y))

//    val position = positionVar as Val<Vec2i>

    val position = combine(getAttr(IntKey.x), getAttr(IntKey.y), ::Vec2i)!!

    val i = wwdObject.i

//    private val imageSetVar = newSimpleVar(wwdObject.imageSet)

    val imageSet = getAttr(StrKey.imageSet)

    val fqImageSetId = imageSet.map { world.expandImageSetId(it) }

    val imageMetadata = fqImageSetId.map { world.supplyMetadata(it!!, i) }

    private val isHighlightedVar = newSimpleVar(false)

    val isHighlighted = isHighlightedVar as Val<Boolean>

    private val isSelectedVar = newSimpleVar(false)

    val isSelected = isSelectedVar as Val<Boolean>

    fun highlight() {
        isHighlightedVar.value = true
    }

    fun unhighlight() {
        isHighlightedVar.value = false
    }

    fun select() {
        isSelectedVar.value = true
    }

    fun unselect() {
        isSelectedVar.value = false
    }

    val boundingBoxLocal = imageMetadata.map {
        Rect2i.fromCenter(it!!.offset, it.size)
    }

    val boundingBox = combine(position, boundingBoxLocal) { positionNow, boundingBoxLocalNow ->
        boundingBoxLocalNow!! + positionNow!!
    }

    init {
        setAttr(IntKey.x, wwdObject.x)
        setAttr(IntKey.y, wwdObject.y)
        setAttr(IntKey.i, wwdObject.i)
        setAttr(StrKey.imageSet, wwdObject.imageSet)
    }
}

private fun <K, V> ObservableMap<K, V>.valAt(key: K): Val<V> {
    return Val.wrap(Bindings.valueAt(this, key))
}

class AttributeMap<T>(
        private val mapMut: ObservableMap<WapObjectAttrKey<T>, T> = observableHashMap()
) : ObservableMap<WapObjectAttrKey<T>, T> by unmodifiableObservableMap(mapMut) {
    internal fun putAttr(attrKey: WapObjectAttrKey<T>, value: T) {
        mapMut[attrKey] = value
    }
}

@Suppress("UNCHECKED_CAST")
fun <T, U : T> uncheckedCast(obj: T) = obj as U
