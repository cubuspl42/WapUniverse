package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import jwap32.v1.Wwd
import jwap32.v1.loadWwd
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import wapuniverse.model.EditorContext
import wapuniverse.model.MainContext
import wapuniverse.rez.RezIndex
import wapuniverse.view.util.observableValue
import java.nio.file.Files
import java.nio.file.Path

class MainContextImpl(
        private val rezIndex: RezIndex
) : MainContext {
    override fun openFile(path: Path) {
        editorContext.bind(observableValue { loadEditorContext(path) })
    }

    override val editorContext = SimpleObjectProperty<EditorContext>()

    private suspend fun loadEditorContext(path: Path): EditorContext {
        val wwd = withContext(CommonPool) { loadWwd(Files.newInputStream(path)) }
        return EditorContextImpl(rezIndex, wwd)
    }
}
