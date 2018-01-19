package wapuniverse.view

import javafx.scene.Parent
import wapuniverse.model.impl.PlaneContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldPresenter(
        private val rezImageProvider: RezImageProvider
) {
    fun presentWorldView(planeContext: PlaneContext): Parent {
        return loadFxml(fxmlFilename) { WorldViewController(planeContext, rezImageProvider) }
    }
}
