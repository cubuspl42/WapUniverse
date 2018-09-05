package wapuniverse.model

import io.github.jwap32.v1.loadWwd
import java.nio.file.Files
import java.nio.file.Path

class EditorFactory() {
    fun createEditor(baseLevel: BaseLevel): Editor {
        TODO()
    }

    fun createEditor(worldPath: Path): Editor {
        val wwd = loadWwd(Files.newInputStream(worldPath))
        return Editor(wwd)
    }
}
