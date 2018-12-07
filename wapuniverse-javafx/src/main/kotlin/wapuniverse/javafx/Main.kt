@file:JvmName("Main")
package wapuniverse.javafx

import javafx.application.Application
import javafx.stage.Stage

class MyApplication : Application() {
    override fun start(primaryStage: Stage) {
        val application = wapuniverse.viewmodel.Application()
        ApplicationPresenter(primaryStage).presentApplication(application)
        application.start()
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApplication::class.java, *args)
}
