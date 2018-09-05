package wapuniverse.model

import java.nio.file.Path

class EditorFactory() {
    fun createEditor(baseLevel: BaseLevel): Editor {
        return Editor(baseLevel)
    }

    fun createEditor(worldPath: Path): Editor {
        return Editor(BaseLevel.LA_ROCA) // TODO
    }
}
