package wapuniverse.model

import javafx.beans.value.ObservableValue
import java.nio.file.Path

interface MainContext {
    fun openFile(path: Path)
    val editorContext: ObservableValue<EditorContext>
}