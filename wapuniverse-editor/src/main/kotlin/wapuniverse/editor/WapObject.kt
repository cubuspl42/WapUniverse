package wapuniverse.editor

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections.*
import javafx.collections.ObservableMap
import org.reactfx.value.Val
import org.reactfx.value.Val.combine
import org.reactfx.value.Var.newSimpleVar
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i
import wapuniverse.editor.WapObjectIntAttrKey as IntKey
import wapuniverse.editor.WapObjectStringAttrKey as StrKey

class WapObject internal constructor(
        val plane: Plane,
        intAttrsInit: Iterable<Pair<IntKey, Int>>,
        strAttrsInit: Iterable<Pair<StrKey, String>>
) {
    data class ExportedAttrs(
            val intAttrs: Map<WapObjectAttrKey<Int>, Int>,
            val strAttrs: Map<WapObjectAttrKey<String>, String>
    )

    private val world = plane.world

    internal val intAttrs = attrMap(IntKey.values().map { it as WapObjectAttrKey<Int> }, 0)

    internal val strAttrs = attrMap(StrKey.values().map { it as WapObjectAttrKey<String> }, "")

    internal fun exportAttrs() = ExportedAttrs(intAttrs.toMap(), strAttrs.toMap())

    internal fun importAttrs(exportedAttrs: ExportedAttrs) {
        intAttrs.clear()
        intAttrs.putAll(exportedAttrs.intAttrs)
        strAttrs.clear()
        strAttrs.putAll(exportedAttrs.strAttrs)
    }

    val x = intAttrs.valAt(IntKey.X)

    val y = intAttrs.valAt(IntKey.Y)

    val position = combine(x, y, ::Vec2i)!!

    val i = intAttrs.valAt(IntKey.I)

    val imageSet = strAttrs.valAt(StrKey.IMAGE_SET)

    val fqImageSetId = imageSet.map { world.expandImageSetId(it) }

    val imageMetadata = Val.combine(fqImageSetId, i) { fqImageSetIdNow, iNow ->
        world.supplyMetadata(fqImageSetIdNow!!, iNow)
    }

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
        intAttrsInit.forEach { this.intAttrs[it.first] = it.second }
        strAttrsInit.forEach { this.strAttrs[it.first] = it.second }
    }
}

private fun <K, V> attrMap(keys: Iterable<WapObjectAttrKey<K>>, defaultValue: V): ObservableMap<WapObjectAttrKey<K>, V> =
        observableMap(keys.map { it to defaultValue }.toMap().toMutableMap())

fun <K, V> ObservableMap<K, V>.valAt(key: K): Val<V> {
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
