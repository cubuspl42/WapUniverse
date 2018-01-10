package wapuniverse

import javafx.application.Application
import javafx.stage.Stage
import wapuniverse.model.impl.MainContextImpl
import wapuniverse.rez.CachingRezImageProvider
import wapuniverse.rez.ClassLoaderRezImageLoader
import wapuniverse.rez.addImageSizes
import wapuniverse.rez.loadYamlRezIndex
import wapuniverse.view.EditorContextPresenter
import wapuniverse.view.MainWindowPresenter
import wapuniverse.view.WorldPresenter
import java.nio.file.Paths

private val rezIndexPath = "rezIndex.yaml"
private val rezImageLoaderPrefix = "CLAW"

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {

        val classLoader = Thread.currentThread().contextClassLoader

        val yamlRezIndex = loadYamlRezIndex(classLoader.getResourceAsStream(rezIndexPath))

        val rezImageLoader = ClassLoaderRezImageLoader(rezImageLoaderPrefix)

        val rezIndex = addImageSizes(yamlRezIndex, rezImageLoader)

        val rezImageProvider = CachingRezImageProvider(rezIndex, rezImageLoader)

        val mainContext = MainContextImpl(rezIndex).apply {
            openFile(Paths.get("F:\\WORLD.WWD"))
        }

        val worldPresenter = WorldPresenter(rezImageProvider)

        val editorContextPresenter = EditorContextPresenter(worldPresenter)

        val mainWindowPresenter = MainWindowPresenter(editorContextPresenter)

        mainWindowPresenter.showMainWindow(mainContext)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
