package wapuniverse.view

import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.loadFxml

private const val FXML = "/view/WorldView.fxml"

fun worldView(planeEditor: PlaneEditor, rezImageProvider: RezImageProvider) =
        loadFxml(FXML) { WorldViewController(planeEditor, rezImageProvider) }
