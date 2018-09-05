package wapuniverse.model

import javafx.beans.property.SimpleStringProperty
import wapuniverse.util.objectProperty

class NewWorldDialog(mainWindow: MainWindow) : Dialog() {
    val baseLevel = objectProperty(BaseLevel.LA_ROCA)

    val name = SimpleStringProperty("")

    val createAction = Action(name.isNotEmpty) {
        close()
        mainWindow.createNewWorld(baseLevel.value ?: BaseLevel.LA_ROCA)
    }
}
