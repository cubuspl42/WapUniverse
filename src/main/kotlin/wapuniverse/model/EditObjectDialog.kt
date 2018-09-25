package wapuniverse.model

import javafx.beans.property.SimpleIntegerProperty
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.geom.Vec2i

class EditObjectDialog(
        private val editObjectContext: EditObjectContext
) : Dialog() {
    val x = SimpleIntegerProperty(editObjectContext.wapObject.position.value.x)

    val y = SimpleIntegerProperty(editObjectContext.wapObject.position.value.y)

    init {
        combine(x, y) { x, y ->
            Vec2i(x.toInt(), y.toInt())
        }.subscribe { _, oldValue, newValue ->
            editObjectContext.setPosition(newValue)
        }
    }

    fun save() {
        close()
    }
}
