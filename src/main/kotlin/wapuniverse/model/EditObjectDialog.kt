package wapuniverse.model

import javafx.beans.property.SimpleIntegerProperty
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.geom.Vec2i

class EditObjectDialog(
        private val editObjectContext: EditObjectContext
) : Dialog() {
    val x = SimpleIntegerProperty(editObjectContext.wapObject.position.value.x)

    val y = SimpleIntegerProperty(editObjectContext.wapObject.position.value.y)

    fun save() {
        combine(x, y) { x, y ->
            editObjectContext.setPosition(Vec2i(x.toInt(), y.toInt()))
        }
        close()
    }
}
