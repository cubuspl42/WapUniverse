package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.loadYamlRezIndex
import wapuniverse.view.showMainWindow

private val rezIndexPath = "rezIndex.yaml"
private val rezImageLoaderPrefix = "CLAW"

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val classLoader = Thread.currentThread().contextClassLoader
        val rezMetadataDao = loadYamlRezIndex(classLoader.getResourceAsStream(rezIndexPath))
        val rezImageLoader = ClassLoaderRezImageLoader(rezImageLoaderPrefix)
        val rezImageProvider = CachingRezImageProvider(rezMetadataDao, rezImageLoader)
        val editorContext = EditorContext()
        showMainWindow(rezImageProvider, editorContext)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
