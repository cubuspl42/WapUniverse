package wapuniverse.javafx.util

import javafx.stage.Stage

class StageProvider(
        primaryStage: Stage
) {
    private val stageIterator = sequence {
        yield(primaryStage)
        while (true) {
            yield(Stage())
        }
    }.iterator()

    fun newStage(): Stage {
        return stageIterator.next()
    }
}
