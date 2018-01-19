package wapuniverse.view

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.BoundingBox
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color.BLUE
import javafx.scene.shape.Rectangle
import org.fxmisc.easybind.EasyBind.listBind
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i
import wapuniverse.model.TileObject
import wapuniverse.model.World
import wapuniverse.model.impl.PlaneContext
import wapuniverse.model.moveToolContext
import wapuniverse.model.selectToolContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.ext.attachController
import wapuniverse.view.ext.map
import wapuniverse.view.ext.toObservableList
import wapuniverse.view.util.observableValue

private val T = 64

class TileObjectPresenter(
        private val rezImageProvider: RezImageProvider,
        private val camera: Camera,
        editorContext: PlaneContext,
        private val world: World
) {
    private val selectToolContext = editorContext.selectToolContext

    private val moveToolContext = editorContext.moveToolContext

    fun presentTileObject(entity: TileObject): Node {
        return group(presentTiles(entity))
    }

    private fun presentTiles(entity: TileObject): ObservableList<Node> {
        val metaTiles = monadic(entity.metaTileGroup.metaTiles)
        return metaTiles.map { tiles ->
            tiles.map { (offset, _) ->
                presentTile(offset, entity)
            }
        }.toObservableList()
    }

    private fun presentTile(offset: Vec2i, entity: TileObject): Node {
        val bbox = entity.tilePosition.map { tilePosition -> tileBbox(tilePosition + offset) }
        val rect = Rectangle().apply {
            xProperty().bind(bbox.map { it.minX })
            yProperty().bind(bbox.map { it.minY })
            width = T.toDouble()
            height = T.toDouble()
            opacity = 0.2
            fill = BLUE

        }
        selectToolContext.attachController { SelectionSurfaceController(rect, it) }
        moveToolContext.attachController { MoveToolObjectController(rect, it) }
        return rect
    }

    private fun provideImage(tileId: Int) = observableValue {
        rezImageProvider.provideImage(
                "LEVEL1_TILES_ACTION", tileId
        )?.image
    }

    fun presentTileObjectUi(entity: TileObject): Group {
        return Group(
                ResizerNode(entity.rect.map { it.scaled(T).toRectangle2D() }, camera.transform).apply {
                    resizeInteraction.addListener { observable,
                                                    oldValue,
                                                    resizeInteraction ->
                        if (resizeInteraction != null) {
                            entity.rect.bind(resizeInteraction.resizedRectangle.map {
                                it!!.toRect2i().scaledDown(T)
                            })
                        } else {
                            entity.rect.unbind()
                        }
                    }
                }
        )
    }

    private fun presentTileHoverUi(offset: Vec2i, entity: TileObject): Node {
        val bbox = SimpleObjectProperty<BoundingBox>(tileBbox(offset))
        return entityRectangle(entity, bbox, camera)
    }

    private fun tileBbox(offset: Vec2i): BoundingBox {
        return BoundingBox(
                offset.x.toDouble() * T, offset.y.toDouble() * T, T.toDouble(), T.toDouble()
        )
    }
}

private fun Rectangle2D.toRect2i() =
        Rect2i(minX.toInt(), minY.toInt(), width.toInt(), height.toInt())

private fun BoundingBox.toRect2i(): Rect2i {
    return Rect2i(minX.toInt(), minY.toInt(), width.toInt(), height.toInt())
}

private fun group(childrenObservableList: ObservableList<Node>): Group {
    return Group().apply {
        properties.put(childrenObservableList, childrenObservableList)
        listBind(children, childrenObservableList)
    }
}
