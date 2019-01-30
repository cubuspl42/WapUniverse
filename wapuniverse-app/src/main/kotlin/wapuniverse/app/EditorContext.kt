package wapuniverse.app

import wapuniverse.editor.Editor
import wapuniverse.editor.World

class EditorContext(
        world: World
) : RootWindowContext() {
    val editor = Editor(world)
}
