package wapuniverse.view

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext

class ResizableCanvas(
        private val drawFunction: (width: Double, height: Double, graphicsContext: GraphicsContext) -> Unit
) : Canvas() {

    init {
        widthProperty().addListener { _ -> redraw() }
        heightProperty().addListener { _ -> redraw() }
    }

    fun redraw() {
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
