package wapuniverse.app

import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import org.reactfx.value.Val
import org.reactfx.value.Var
import wapuniverse.app.world_preview.tileLength
import wapuniverse.editor.TileModeContext
import wapuniverse.editor.extensions.forEach
import wapuniverse.rez.RezImageCache

private data class TileId(val value: Int)

fun tilePicker(
        context: TileModeContext,
        rezImageCache: RezImageCache
): Val<Node?> {
    val plane = context.plane

    fun tileImage(tileId: Int) =
            ImageView().apply {
                fitWidth = tileLength.toDouble()
                fitHeight = tileLength.toDouble()
                imageProperty().bind(plane.fqImageSetId.map {
                    rezImageCache.getImage(it, tileId)?.image
                })
            }

    fun text(tileId: Int) = tileId.toString()

    return plane.tileSet.map { items ->
        listView(
                observableList(items!!),
                context.tileId,
                graphics = ::tileImage,
                text = ::text
        ).apply {
            orientation = Orientation.HORIZONTAL
            prefHeight = 128.0
        } as Node
    }
}

private fun <E> listViewBase(
        items: ObservableList<E>,
        selectedItem: Var<E>,
        graphics: (E) -> Node? = { null },
        text: (E) -> Val<String>
) =
        ListView(items).apply {
            selectedItem.forEach {
                selectionModel.select(it)
            }
            selectionModel.selectedItemProperty().forEach {
                selectedItem.value = it
            }
            setCellFactory { _ ->
                object : ListCell<E>() {
                    override fun updateItem(item: E?, empty: Boolean) {
                        super.updateItem(item, empty)
                        item?.let { textProperty().bind(text(it)) }
                        this.graphic = item?.let(graphics)
                    }
                }
            }
        }

fun <E> listView(
        items: ObservableList<E>,
        selectedItem: Var<E>,
        graphics: (E) -> Node? = { null },
        text: (E) -> Val<String>
) = listViewBase(items, selectedItem, graphics, text)

@JvmName("listView2")
fun <E> listView(
        items: ObservableList<E>,
        selectedItem: Var<E>,
        graphics: (E) -> Node? = { null },
        text: (E) -> String
) = listViewBase(items, selectedItem, graphics) { Val.constant(text(it)) }
