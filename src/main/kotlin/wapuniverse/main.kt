package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.MainWindow
import wapuniverse.view.mainWindow

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val mainWindowModel = MainWindow()
        mainWindow(primaryStage, mainWindowModel)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
