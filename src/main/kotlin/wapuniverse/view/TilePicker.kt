package wapuniverse.view

import javafx.collections.FXCollections.observableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import wapuniverse.model.PencilToolContext
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.forEach
import wapuniverse.view.util.observableValue

private data class TileId(val value: Int)

fun tilePicker(
        pencilToolContext: PencilToolContext,
        rezImageProvider: RezImageProvider
): Node? {
    val plane = pencilToolContext.plane

    fun tileImage(tileId: Int) =
            ImageView().apply {
                imageProperty().bind(observableValue {
                    val rezPath = plane.findTileImageMetadata(tileId)?.rezPath ?: return@observableValue null
                    rezImageProvider.provideImage(rezPath)
                })
            }

    val tileset = plane.tileset ?: return null

    val listView = ListView<TileId>(observableList(tileset.map(::TileId)))

    pencilToolContext.tileId.forEach { tileId ->
        listView.selectionModel.select(tileId)
    }

    listView.selectionModel.selectedItemProperty().forEach {
        pencilToolContext.setTileId(it.value)
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
