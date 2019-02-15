package wapuniverse.app

import javafx.collections.FXCollections.observableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import wapuniverse.app.world_preview.tileLength
import wapuniverse.editor.TileModeContext
import wapuniverse.editor.extensions.forEach
import wapuniverse.rez.RezImageCache

private data class TileId(val value: Int)

fun tilePicker(
        context: TileModeContext,
        rezImageCache: RezImageCache
): Node? {
    val plane = context.plane

    fun tileImage(tileId: Int) =
            ImageView().apply {
                fitWidth = tileLength.toDouble()
                fitHeight = tileLength.toDouble()
                image = rezImageCache.getImage(plane.fqImageSetId, tileId)?.image
            }

    val listView = ListView<TileId>(observableList(plane.tileSet?.map(::TileId) ?: emptyList()))

    context.tileId.forEach { tileId ->
        listView.selectionModel.select(TileId(tileId))
    }

    listView.selectionModel.selectedItemProperty().forEach {
        context.tileId.value = it.value
    }

    return listView.apply {
        orientation = Orientation.HORIZONTAL
        prefHeight = 128.0
        setCellFactory { _ ->
            object : ListCell<TileId>() {
                override fun updateItem(item: TileId?, empty: Boolean) {
                    super.updateItem(item, empty)
                    val tileIdVal = item?.value
                    graphic = tileIdVal?.let(::tileImage)
                    text = tileIdVal?.toString()
                }
            }
        }
    }
}
