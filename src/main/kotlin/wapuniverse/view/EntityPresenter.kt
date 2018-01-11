package wapuniverse.view

import javafx.scene.Node
import wapuniverse.model.Entity
import wapuniverse.model.TileObject
import wapuniverse.model.WapObject

class EntityPresenter(
        private val wapObjectPresenter: WapObjectPresenter,
        private val tileObjectPresenter: TileObjectPresenter
) {
    fun presentEntity(entity: Entity): Node =
            when (entity) {
                is WapObject -> wapObjectPresenter.presentObjectImageView(entity)
                is TileObject -> tileObjectPresenter.presentTileObject(entity)
                else -> throw AssertionError()
            }

    fun presentEntityUi(entity: Entity) =
            when (entity) {
                is WapObject -> wapObjectPresenter.presentObjectUi(entity)
                is TileObject -> tileObjectPresenter.presentTileObjectUi(entity)
                else -> throw AssertionError()
            }
}
