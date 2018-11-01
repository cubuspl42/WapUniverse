package wapuniverse.view

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.transform.Affine
import wapuniverse.model.util.Disposable

class ResizableCanvas(
        parent: Disposable,
        private val drawFunction: (width: Double, height: Double, graphicsContext: GraphicsContext) -> Unit
) : Canvas() {
    private val timer = object : AnimationTimer() {
        override fun handle(now: Long) {
            draw()
        }
    }

    init {
        widthProperty().addListener { _ -> draw() }
        heightProperty().addListener { _ -> draw() }
        parent.addDisposeListener { timer.stop() }
        timer.start()
    }

    private fun draw() {
        val gc = graphicsContext2D
        gc.save()
        gc.clearRect(0.0, 0.0, width, height)
        drawFunction(width, height, gc)
        gc.restore()
    }

    override fun isResizable(): Boolean {
        return true
    }

    override fun prefWidth(height: Double): Double {
        return width
    }

    override fun prefHeight(width: Double): Double {
        return height
    }
}
