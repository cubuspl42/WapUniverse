package wapuniverse.model

import java.nio.file.Path

class OpenWorldDialog(private val mainWindow: MainWindow) : Dialog() {
    fun open(worldPath: Path) {
        close()
        mainWindow.openWorld(worldPath)
    }
}
