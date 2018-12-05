@file:JvmName("Main")
package wapuniverse.javafx

import javafx.application.Application
import javafx.stage.Stage

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val presenter = JavaFxApplicationPresenter(primaryStage)
        wapuniverse.application.Application(presenter)
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
