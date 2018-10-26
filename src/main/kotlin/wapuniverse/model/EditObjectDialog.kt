package wapuniverse.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.geom.Vec2i

class EditObjectDialog(
        private val editObjectContext: EditObjectContext
) : Dialog() {
    private val wapObject = editObjectContext.wapObject

    private val data = wapObject.data.value

    val id = SimpleIntegerProperty(data.id)

    val name = SimpleStringProperty(data.name)

    val logic = SimpleStringProperty(data.logic)

    val imageSet = SimpleStringProperty(data.imageSet)

    val animation = SimpleStringProperty(data.animation)

    val x = SimpleIntegerProperty(data.x)

    val y = SimpleIntegerProperty(data.y)

    val z = SimpleIntegerProperty(data.z)

    val i = SimpleIntegerProperty(data.i)

    val score = SimpleIntegerProperty(data.score)

    val points = SimpleIntegerProperty(data.points)

    val smarts = SimpleIntegerProperty(data.smarts)

    val powerup = SimpleIntegerProperty(data.powerup)

    val damage = SimpleIntegerProperty(data.damage)

    val health = SimpleIntegerProperty(data.health)

    val speedX = SimpleIntegerProperty(data.speedX)

    val speedY = SimpleIntegerProperty(data.speedY)

    val faceDir = SimpleIntegerProperty(data.faceDir)

    val xMin = SimpleIntegerProperty(data.xMin)

    val xMax = SimpleIntegerProperty(data.xMax)

    val direction = SimpleIntegerProperty(data.direction)

    val yMin = SimpleIntegerProperty(data.yMin)

    val yMax = SimpleIntegerProperty(data.yMax)

    val speed = SimpleIntegerProperty(data.speed)

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
