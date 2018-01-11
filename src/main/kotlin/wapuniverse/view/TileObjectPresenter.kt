package wapuniverse.view

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import org.fxmisc.easybind.EasyBind.listBind
import org.fxmisc.easybind.monadic.MonadicBinding
import wapuniverse.geom.Vec2i
import wapuniverse.model.TileObject
import wapuniverse.model.impl.resolveShortId
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.map
import wapuniverse.view.ext.singletonObservableList
import wapuniverse.view.ext.toObservableList
import wapuniverse.view.ext.toObservableValue
import wapuniverse.view.util.observableValue

private val T = 64

class TileObjectPresenter(
        private val rezImageProvider: RezImageProvider,
        private val camera: Camera
) {
    fun presentTileObject(entity: TileObject): Node {
        return Group().apply {
            listBind(children, presentTiles(entity))
            translateXProperty().bind(entity.position.map { it.x })
            translateYProperty().bind(entity.position.map { it.y })
        }
    }

    private fun presentTiles(entity: TileObject): ObservableList<out Node> {
        return entity.tiles.toObservableValue().map { tiles ->
            tiles.map { (offset, tileId) ->
                presentTile(offset, tileId)
            }
        }.toObservableList()
    }

    private fun presentTile(offset: Vec2i, tileId: Int): ImageView {
        return ImageView().apply {
            x = offset.x.toDouble() * T
            y = offset.y.toDouble() * T

            imageProperty().bind(provideImage(tileId))

            isMouseTransparent = true
        }
    }

    private fun provideImage(tileId: Int) = observableValue {
        rezImageProvider.provideImage(
                resolveShortId("LEVEL1_TILES_ACTION"), tileId
        )?.image
    }

    fun presentTileObjectUi(entity: TileObject): Node {
        return Group().apply {
            listBind(children, singletonObservableList(presentTilesUi(entity)))
        }
    }

    private fun presentTilesUi(entity: TileObject): MonadicBinding<Group> {
        return entity.isHovered.map { isHovered ->
            if (isHovered) {
                presentTilesHoverUi(entity)
            } else {
                Group()
            }
        }
    }

    private fun presentTilesHoverUi(entity: TileObject): Group {
        return Group().apply {
            listBind(children, entity.tiles.toObservableValue().map { tiles ->
                tiles.map { (offset, tileId) ->
                    presentTileHoverUi(offset, tileId)
                }
            }.toObservableList())
        }
    }

    private fun presentTileHoverUi(offset: Vec2i, tileId: Int): Node {
        val bbox = SimpleObjectProperty<BoundingBox>(BoundingBox(
                offset.x.toDouble() * T, offset.y.toDouble() * T, T.toDouble(), T.toDouble()
        ))
        return presentRectangle(bbox, camera.transform).apply {
            fill = Color.TRANSPARENT
            stroke = Color.ALICEBLUE
        }
    }
}
