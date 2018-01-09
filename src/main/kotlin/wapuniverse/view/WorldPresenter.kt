package wapuniverse.view

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import wapuniverse.model.EditorContext
import wapuniverse.model.MoveToolContext
import wapuniverse.model.SelectToolContext
import wapuniverse.model.ToolContext
import wapuniverse.model.World
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.mapTo
import wapuniverse.view.util.loadFxml

private val fxmlFilename = "/fxml/WorldView.fxml"

class WorldPresenter(
        private val editorContext: EditorContext,
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
        val worldContent = Group(presentWorldBackground())
        return world.objects.mapTo(worldContent) {
            wapObjectPresenter.presentObjectImageView(it)
        }
    }

    private fun presentWorldBackground(): Node {
        val backgroundRect = Rectangle(-128.0, -128.0, 1024.0, 1024.0).apply {
            opacity = 0.1
        }
        WorldBackgroundController(backgroundRect, editorContext)
        return backgroundRect
    }

    private fun presentWorldUi(): Group {
        val worldUi = Group()
        world.objects.mapTo(worldUi) {
            wapObjectPresenter.presentObjectUi(it)
        }
        editorContext.activeToolContext.mapTo(worldUi) {
            presentActiveToolContext(it)
        }
        return worldUi
    }

    private fun presentActiveToolContext(toolContext: ToolContext): Node {
        return when (toolContext) {
            is SelectToolContext -> presentSelectToolContext(toolContext)
            is MoveToolContext -> presentMoveToolContext(toolContext)
            else -> throw AssertionError()
        }
    }

    private fun presentSelectToolContext(toolContext: SelectToolContext): Node {
        val selectToolUi = Group()
        return toolContext.areaSelection.mapTo(selectToolUi) {
            presentRectangle(it.boundingBox, camera.transform).apply {
                fill = Color.NAVY
                stroke = Color.CYAN
                opacity = 0.3
            }
        }
    }

    private fun presentMoveToolContext(toolContext: MoveToolContext): Node {
        return Group()
    }
}
