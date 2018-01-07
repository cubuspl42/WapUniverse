package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.loadYamlRezIndex
import wapuniverse.view.MainWindowPresenter
import wapuniverse.view.WapObjectPresenter
import wapuniverse.view.WorldPresenter

private val rezIndexPath = "rezIndex.yaml"
private val rezImageLoaderPrefix = "CLAW"

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val editorContext = EditorContext()

        val classLoader = Thread.currentThread().contextClassLoader

        val rezIndex = loadYamlRezIndex(classLoader.getResourceAsStream(rezIndexPath))

        val rezImageLoader = ClassLoaderRezImageLoader(rezImageLoaderPrefix)

        val rezImageProvider = CachingRezImageProvider(rezIndex, rezImageLoader)

        val wapObjectPresenter = WapObjectPresenter(rezImageProvider)

        val worldPresenter = WorldPresenter(wapObjectPresenter)

        val mainWindowPresenter = MainWindowPresenter(worldPresenter)

        mainWindowPresenter.showMainWindow(editorContext)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
