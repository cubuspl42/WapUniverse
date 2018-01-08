package wapuniverse.view

import javafx.scene.Group
import javafx.scene.Parent
import wapuniverse.model.EditorContext
import wapuniverse.model.World
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.mapTo
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldPresenter(
        private val world: World,
        rezImageProvider: RezImageProvider
) {
    private val camera = Camera()

    private val wapObjectPresenter = WapObjectPresenter(rezImageProvider, camera)

    fun presentWorldView(editorContext: EditorContext): Parent {
        val contentNode = presentWorldContent()
        val uiNode = presentWorldUi()
        return loadFxml(fxmlFilename) { WorldViewController(contentNode, uiNode, camera, editorContext) }
    }

    private fun presentWorldContent(): Group {
        return world.objects.mapTo(Group()) {
            wapObjectPresenter.presentObjectImageView(it)
        }
    }

    private fun presentWorldUi(): Group {
        return world.objects.mapTo(Group()) {
            wapObjectPresenter.presentObjectUi(it)
        }
    }
}
