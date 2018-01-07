package wapuniverse.view

import javafx.scene.Group
import javafx.scene.Parent
import wapuniverse.model.World
import wapuniverse.view.ext.mapTo
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldPresenter(
        private val world: World,
        private val wapObjectPresenter: WapObjectPresenter
) {
    fun presentWorldView(): Parent {
        val contentNode = presentWorldGroup()
        return loadFxml(fxmlFilename) { WorldViewController(contentNode) }
    }

    private fun presentWorldGroup(): Group {
        return world.objects.mapTo(Group()) {
            wapObjectPresenter.presentObjectImageView(it)
        }
    }
}
