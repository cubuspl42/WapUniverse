package wapuniverse.app

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.rez.buildRezImageCache
import wapuniverse.rez.loadRezIndex

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val rezIndex = loadRezIndex()
        val rezImageCache = buildRezImageCache(rezIndex)
        RootWindow(primaryStage, rezImageCache)
    }
}
