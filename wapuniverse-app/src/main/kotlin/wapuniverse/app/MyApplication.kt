package wapuniverse.app

import javafx.application.Application
import javafx.stage.Stage

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        RootWindow(primaryStage)
    }
}
