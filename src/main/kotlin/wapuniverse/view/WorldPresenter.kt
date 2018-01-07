package wapuniverse.view

import javafx.scene.Group
import wapuniverse.model.World
import wapuniverse.view.ext.mapTo

class WorldPresenter(
        private val wapObjectPresenter: WapObjectPresenter
) {
    fun presentWorldGroup(world: World): Group {
        return world.objects.mapTo(Group()) {
            wapObjectPresenter.presentObjectImageView(it)
        }
    }
}
