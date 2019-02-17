package wapuniverse.app

import javafx.scene.Scene
import javafx.stage.Stage
import wapuniverse.editor.Editor
import wapuniverse.editor.World
import wapuniverse.util.intTextField
import wapuniverse.util.stringTextField
import wapuniverse.util.twoColumnForm

class WorldDialog(editor: Editor) {
    private val window = Stage().apply {
        title = "World"
        scene = Scene(worldDialogUi(editor.world))
        show()
    }
}

private fun worldDialogUi(world: World) =
        twoColumnForm(
                "Level name" to stringTextField(world.levelName),
                "Author" to stringTextField(world.author),
                "Birth" to stringTextField(world.birth),
                "REZ file" to stringTextField(world.rezFile),
                "Image dir" to stringTextField(world.imageDir),
                "Pal rez" to stringTextField(world.palRez),
                "Start X" to intTextField(world.startPosition.x),
                "Start Y" to intTextField(world.startPosition.y),
                "Launch app" to stringTextField(world.launchApp)
        )
