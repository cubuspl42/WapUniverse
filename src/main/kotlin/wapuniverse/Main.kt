package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.EditorContext
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.addImageSizes
import wapuniverse.rez.loadYamlRezIndex
import wapuniverse.view.MainWindowPresenter
import wapuniverse.view.WorldPresenter

private val rezIndexPath = "rezIndex.yaml"
private val rezImageLoaderPrefix = "CLAW"

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {

        val classLoader = Thread.currentThread().contextClassLoader

        val yamlRezIndex = loadYamlRezIndex(classLoader.getResourceAsStream(rezIndexPath))

        val rezImageLoader = ClassLoaderRezImageLoader(rezImageLoaderPrefix)

        val rezIndex = addImageSizes(yamlRezIndex, rezImageLoader)

        val rezImageProvider = CachingRezImageProvider(rezIndex, rezImageLoader)

        val editorContext = EditorContext(rezIndex)

        val editorContextPresenter = EditorContextPresenter(editorContext)

        val worldPresenter = WorldPresenter(editorContext, editorContext.world, rezImageProvider)

        val mainWindowPresenter = MainWindowPresenter(editorContextPresenter, worldPresenter)

        mainWindowPresenter.showMainWindow(editorContext)
    }
}

class EditorContextPresenter(
        editorContext: EditorContext
) {

}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
