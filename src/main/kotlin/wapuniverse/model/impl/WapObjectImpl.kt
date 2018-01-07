package wapuniverse.model.impl

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import wapuniverse.model.WapObject

class WapObjectImpl : WapObject {
    override val imageSet = SimpleStringProperty("LEVEL1_OFFICER")
    override val x = SimpleIntegerProperty()
    override val y = SimpleIntegerProperty()
}
