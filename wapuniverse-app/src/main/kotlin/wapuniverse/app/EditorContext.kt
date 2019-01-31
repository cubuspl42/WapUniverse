package wapuniverse.app

import wapuniverse.editor.Editor
import wapuniverse.editor.World
import wapuniverse.rez.RezImageCache

class EditorContext(
        world: World,
        rezImageCache: RezImageCache
) : RootWindowContext() {
    val editor = Editor(world)

    val image = rezImageCache.getImage("LEVEL1_IMAGES_OFFICER", -1)?.image
}
