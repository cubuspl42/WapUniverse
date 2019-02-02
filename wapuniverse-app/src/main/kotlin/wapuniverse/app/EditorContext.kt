package wapuniverse.app

import wapuniverse.editor.Editor
import wapuniverse.editor.World
import wapuniverse.rez.RezImageCache

class EditorContext(
        world: World,
        val rezImageCache: RezImageCache
) : RootWindowContext() {
    val editor = Editor(world)
}
