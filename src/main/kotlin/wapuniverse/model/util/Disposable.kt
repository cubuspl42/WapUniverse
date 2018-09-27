package wapuniverse.model.util

open class Disposable(
        parent: Disposable? = null
) {
    private val children = mutableListOf<Disposable>()

    init {
        parent?.addChild(this)
    }

    fun dispose() {
        uninit()
        children.forEach { it.dispose() }
    }

    fun addChild(child: Disposable) {
        children.add(child)
    }

    inline fun addDisposeListener(crossinline function: () -> Unit) {
        addChild(disposable(function))
    }

    protected open fun uninit() {}
}

inline fun disposable(crossinline function: () -> Unit): Disposable {
    return object : Disposable() {
        override fun uninit() {
            function()
        }
    }
}
