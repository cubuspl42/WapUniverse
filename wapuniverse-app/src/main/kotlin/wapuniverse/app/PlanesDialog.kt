package wapuniverse.app

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.reactfx.value.Val
import org.reactfx.value.Var
import wapuniverse.editor.Editor
import wapuniverse.editor.Plane
import wapuniverse.util.button
import wapuniverse.util.stringTextField
import wapuniverse.util.twoColumnForm

class PlanesDialog(editor: Editor) {
    val selectedPlane = Var.newSimpleVar(editor.activePlane.value)

    private val window = Stage().apply {
        title = "Planes"
        scene = Scene(planesDialogUi(editor, selectedPlane))
//        setOnCloseRequest { editObjectContext.abort() }
        show()
    }

    fun editPlane() {
        Stage().apply {
            title = "Edit Plane"
            scene = Scene(editPlaneDialogUi(selectedPlane.value))
            show()
        }
    }
}

private fun PlanesDialog.planesDialogUi(editor: Editor, selectedPlane: Var<Plane>): Parent? {
    fun function(it: Plane) = it.name as Val<String>
    return VBox(
            listView(editor.world.planes, selectedPlane, text = ::function),
            HBox(button("Edit", this::editPlane))
    )
}

//private fun listView(editor: Editor): ListView<Plane> {
//    val listView = listView(editor.world.planes, )
//
//    return listView.apply {
//        setCellFactory { _ ->
//            object : ListCell<Plane>() {
//                override fun updateItem(item: Plane?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                    textProperty().bind(item?.name)
//                }
//            }
//        }
//    }
//}

private fun editPlaneDialogUi(plane: Plane): Parent? {
    return twoColumnForm(
            "Name" to stringTextField(plane.name)
    )
}