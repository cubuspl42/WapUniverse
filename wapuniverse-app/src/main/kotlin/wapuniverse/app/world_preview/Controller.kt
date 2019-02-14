package wapuniverse.app.world_preview

import javafx.scene.Node
import javafx.scene.input.KeyCodeCombination
import wapuniverse.editor.util.Disposable

open class Controller(
        parent: Disposable,
        private val node: Node? = null
) : Disposable(parent) {
    protected fun accelerator(kc: KeyCodeCombination, function: () -> Unit) {
        check(!isDisposed)
        node?.scene?.let { scene ->
            scene.accelerators[kc] = Runnable(function)
            onDisposed.subscribe { scene.accelerators.remove(kc) }
        }
    }
}
