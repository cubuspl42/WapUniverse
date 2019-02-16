package wapuniverse.editor

import io.github.jwap32.v1.WwdObject
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections.observableMap
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

    val isActive = world.editor.flatMap { editor -> editor.mode.map { it == Mode.OBJECT } }

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

    val fqImageSetId = imageSet.map { world.expandImageSetId(it) }!!

    val imageMetadata = Val.combine(fqImageSetId, i) { fqImageSetIdNow, iNow ->
        world.supplyMetadata(fqImageSetIdNow!!, iNow)
    }!!

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

    internal fun toWwdObject(): WwdObject { // TODO
        return WwdObject(
//                id = intAttrs[IntKey.ID],
                x = intAttrs[IntKey.X] ?: 0,
                y = intAttrs[IntKey.Y] ?: 0,
                z = intAttrs[IntKey.Z] ?: 0,
                i = intAttrs[IntKey.I] ?: 0,
//        addFlags:  = WwdObjectAddFlags(), /* WAP_OBJECT_ADD_FLAG_ flags */
//        dynamicFlags: WwdObjectDynamicFlags = WwdObjectDynamicFlags(), /* WAP_OBJECT_DYNAMIC_FLAG_ flags */
//        drawFlags: WwdObjectDrawFlags = WwdObjectDrawFlags(), /* WAP_OBJECT_DRAW_FLAG_ flags */
//        userFlags: WwdObjectUserFlags = WwdObjectUserFlags(), /* WAP_OBJECT_USER_FLAG_ flags */
                score = intAttrs[IntKey.SCORE] ?: 0,
                points = intAttrs[IntKey.POINTS] ?: 0,
                powerup = intAttrs[IntKey.POWERUP] ?: 0,
                damage = intAttrs[IntKey.DAMAGE] ?: 0,
                smarts = intAttrs[IntKey.SMARTS] ?: 0,
                health = intAttrs[IntKey.HEALTH] ?: 0,
//        moveRect = WwdRect(),
//        hitRect = WwdRect(),
//        attackRect = WwdRect(),
//        clipRect = WwdRect(),
//        userRect1 = WwdRect(),
//        userRect2 = WwdRect(),
                // userValue1 = intAttrs[IntKey.X] ?: 0,
                // userValue2 = intAttrs[IntKey.X] ?: 0,
                // userValue3 = intAttrs[IntKey.X] ?: 0,
                // userValue4 = intAttrs[IntKey.X] ?: 0,
                // userValue5 = intAttrs[IntKey.X] ?: 0,
                // userValue6 = intAttrs[IntKey.X] ?: 0,
                // userValue7 = intAttrs[IntKey.X] ?: 0,
                // userValue8 = intAttrs[IntKey.X] ?: 0,
                xMin = intAttrs[IntKey.X_MIN] ?: 0,
                yMin = intAttrs[IntKey.Y_MIN] ?: 0,
                xMax = intAttrs[IntKey.X_MAX] ?: 0,
                yMax = intAttrs[IntKey.Y_MAX] ?: 0,
                speedX = intAttrs[IntKey.SPEED_X] ?: 0,
                speedY = intAttrs[IntKey.SPEED_Y] ?: 0,
//                xTweak = intAttrs[IntKey.X_TWEAK] ?: 0,
//                yTweak = intAttrs[IntKey.Y_TWEAK] ?: 0,
//                counter = intAttrs[IntKey.COUNTER] ?: 0,
                speed = intAttrs[IntKey.SPEED] ?: 0,
//                width = intAttrs[IntKey.WIDTH] ?: 0,
//                height = intAttrs[IntKey.HEIGHT] ?: 0,
                direction = intAttrs[IntKey.DIRECTION] ?: 0,
                faceDir = intAttrs[IntKey.FACEDIR] ?: 0,
//                timeDelay = intAttrs[IntKey.TIME_DELAY] ?: 0,
//                frameDelay = intAttrs[IntKey.FRAME_DELAY] ?: 0,
                objectType = intAttrs[IntKey.X] ?: 0,
                hitTypeFlags = intAttrs[IntKey.X] ?: 0,
                xMoveRes = intAttrs[IntKey.X] ?: 0,
                yMoveRes = intAttrs[IntKey.X] ?: 0,
                name = strAttrs[StrKey.NAME] ?: "",
                logic = strAttrs[StrKey.LOGIC] ?: "",
                imageSet = strAttrs[StrKey.IMAGE_SET] ?: "",
                animation = strAttrs[StrKey.ANIMATION] ?: ""
        )
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
