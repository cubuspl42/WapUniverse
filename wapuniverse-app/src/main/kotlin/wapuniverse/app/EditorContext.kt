package wapuniverse.app

import wapuniverse.editor.Editor
import wapuniverse.editor.World
import wapuniverse.editor.extensions.flatMap
import wapuniverse.editor.extensions.forEach
import wapuniverse.rez.RezImageCache

class EditorContext(
        private val rootWindow: RootWindow,
        world: World,
        val rezImageCache: RezImageCache
) : RootWindowContext() {
    val editor = Editor(world)

    init {
        editor.activePlaneContext
                .flatMap { it.objectModeContext }
                .flatMap { it.editObjectContext }
                .forEach { EditObjectDialog(it) }
    }
}
