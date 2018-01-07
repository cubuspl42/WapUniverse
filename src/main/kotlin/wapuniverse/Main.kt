package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.view.showMainWindow

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val editorContext = EditorContext()
        showMainWindow(editorContext)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
